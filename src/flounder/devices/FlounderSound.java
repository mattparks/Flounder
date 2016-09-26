package flounder.devices;

import flounder.engine.*;
import flounder.engine.entrance.*;
import flounder.logger.*;
import flounder.maths.vectors.*;
import flounder.processing.*;
import flounder.profiling.*;
import flounder.resources.*;
import flounder.sounds.*;
import org.lwjgl.openal.*;

import java.nio.*;

import static org.lwjgl.openal.AL.*;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * An Sound Device implemented using OpenAL.
 */
public class FlounderSound extends IModule {
	public static final MyFile SOUND_FOLDER = new MyFile(MyFile.RES_FOLDER, "sounds");
	private static final FlounderSound instance = new FlounderSound();

	private long device;

	private Vector3f cameraPosition;
	private SourcePoolManager sourcePool;
	private StreamManager streamManager;
	private MusicPlayer musicPlayer;

	private FlounderSound() {
		super(FlounderLogger.class, FlounderProfiler.class, FlounderProcessors.class);
		cameraPosition = new Vector3f();
	}

	@Override
	public void init() {
		// Creates the OpenAL contexts.
		device = alcOpenDevice((ByteBuffer) null);
		ALCCapabilities deviceCaps = ALC.createCapabilities(device);
		alcMakeContextCurrent(alcCreateContext(device, (IntBuffer) null));
		createCapabilities(deviceCaps);

		// Checks for errors.
		int alError = alGetError();

		if (alError != GL_NO_ERROR) {
			FlounderLogger.error("OpenAL Error " + alError);
		}

		// Creates a new model and main objects.
		alDistanceModel(AL11.AL_LINEAR_DISTANCE_CLAMPED);
		sourcePool = new SourcePoolManager();
		streamManager = new StreamManager();
		streamManager.start();
		musicPlayer = new MusicPlayer();
		musicPlayer.setVolume(MusicPlayer.SOUND_VOLUME);
	}

	@Override
	public void update() {
		ICamera camera = FlounderEngine.getCamera();

		if (camera != null && camera.getPosition() != null) {
			cameraPosition.set(camera.getPosition());
			alListener3f(AL10.AL_POSITION, cameraPosition.x, cameraPosition.y, cameraPosition.z);
			musicPlayer.update(FlounderEngine.getDelta());
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
	public void dispose() {
		streamManager.kill();
		sourcePool.dispose();
		musicPlayer.cleanUp();
		SoundLoader.dispose();
		alcCloseDevice(device);
	}
}
