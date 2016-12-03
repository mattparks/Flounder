package flounder.devices;

import flounder.camera.*;
import flounder.framework.*;
import flounder.logger.*;
import flounder.maths.vectors.*;
import flounder.processing.*;
import flounder.profiling.*;
import flounder.resources.*;
import flounder.sounds.*;
import org.lwjgl.openal.*;

import java.nio.*;

import static org.lwjgl.openal.AL.createCapabilities;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.AL11.*;
import static org.lwjgl.openal.ALC.createCapabilities;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * A module used for loading, managing and playing a variety of different sound types.
 */
public class FlounderSound extends IModule {
	public static final MyFile SOUND_FOLDER = new MyFile(MyFile.RES_FOLDER, "sounds");
	private static final FlounderSound instance = new FlounderSound();

	private Vector3f cameraPosition;
	private long device;

	private SourcePoolManager sourcePool;
	private StreamManager streamManager;
	private MusicPlayer musicPlayer;

	/**
	 * Creates a new OpenAL audio manager.
	 */
	public FlounderSound() {
		super(ModuleUpdate.UPDATE_PRE, FlounderLogger.class, FlounderProfiler.class, FlounderProcessors.class);
	}

	@Override
	public void init() {
		this.cameraPosition = new Vector3f();

		// Creates the OpenAL contexts.
		this.device = alcOpenDevice((ByteBuffer) null);
		ALCCapabilities deviceCaps = createCapabilities(device);
		alcMakeContextCurrent(alcCreateContext(device, (IntBuffer) null));
		createCapabilities(deviceCaps);

		// Checks for errors.
		int alError = alGetError();

		if (alError != GL_NO_ERROR) {
			FlounderLogger.error("OpenAL Error " + alError);
		}

		// Creates a new model and main objects.
		alDistanceModel(AL_LINEAR_DISTANCE_CLAMPED);
		this.sourcePool = new SourcePoolManager();
		this.streamManager = new StreamManager();
		streamManager.start();
		this.musicPlayer = new MusicPlayer();
		musicPlayer.setVolume(MusicPlayer.SOUND_VOLUME);
	}

	@Override
	public void update() {
		ICamera camera = FlounderCamera.getCamera();

		if (camera != null && camera.getPosition() != null) {
			cameraPosition.set(camera.getPosition());
			alListener3f(AL10.AL_POSITION, cameraPosition.x, cameraPosition.y, cameraPosition.z);
			musicPlayer.update(FlounderFramework.getDelta());
			sourcePool.update();
		}
	}

	@Override
	public void profile() {
	}

	/**
	 * Gets the cameras position.
	 *
	 * @return The cameras position.
	 */
	public static Vector3f getCameraPosition() {
		return instance.cameraPosition;
	}

	/**
	 * Plays a sound that should be emitted from somewhere in the 3D world.
	 *
	 * @param playRequest The request containing the sound and all the settings for the playing of the sound.
	 *
	 * @return The controller for the source which plays the sound. Returns {@code null} if no source was available to play the sound.
	 */
	public static AudioController play3DSound(PlayRequest playRequest) {
		if (playRequest.getSound() != null && !playRequest.getSound().isLoaded()) {
			return null;
		}

		return instance.sourcePool.play(playRequest);
	}

	/**
	 * Send a request to play a system sound effect at full volume. The request is sent to the {@link SourcePoolManager} which will find a source to play the sound.
	 *
	 * @param sound The sound to be played.
	 *
	 * @return The controller for the playing of this sound.
	 */
	public static AudioController playSystemSound(Sound sound) {
		if (sound != null && !sound.isLoaded()) {
			return null;
		}

		return instance.sourcePool.play(PlayRequest.newSystemPlayRequest(sound));
	}

	/**
	 * Gets the sound stream manager.
	 *
	 * @return The sound stream manager.
	 */
	public static StreamManager getStreamManager() {
		return instance.streamManager;
	}

	/**
	 * Gets the background music player.
	 *
	 * @return The background music player.
	 */
	public static MusicPlayer getMusicPlayer() {
		return instance.musicPlayer;
	}

	@Override
	public IModule getInstance() {
		return instance;
	}

	@Override
	public void dispose() {
		streamManager.kill();
		sourcePool.dispose();
		musicPlayer.dispose();
		SoundLoader.dispose();
		alcCloseDevice(device);
	}
}
