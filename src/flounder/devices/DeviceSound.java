package flounder.devices;

import flounder.engine.*;
import flounder.engine.implementation.*;
import flounder.maths.vectors.*;
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
public class DeviceSound implements IModule {
	public static final MyFile SOUND_FOLDER = new MyFile(MyFile.RES_FOLDER, "sounds");

	private long device;

	private Vector3f cameraPosition;
	private SourcePoolManager sourcePool;
	private StreamManager streamManager;
	private MusicPlayer musicPlayer;

	/**
	 * Creates a new OpenGL sound manager.
	 */
	protected DeviceSound() {
		cameraPosition = new Vector3f(0.0f, 0.0f, 0.0f);
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
			FlounderEngine.getLogger().error("OpenAL Error " + alError);
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
	public Vector3f getCameraPosition() {
		return cameraPosition;
	}

	/**
	 * Plays a sound that should be emitted from somewhere in the 3D world.
	 *
	 * @param playRequest The request containing the sound and all the settings for the playing of the sound.
	 *
	 * @return The controller for the source which plays the sound. Returns {@code null} if no source was available to play the sound.
	 */
	public AudioController play3DSound(PlayRequest playRequest) {
		if (playRequest.getSound() != null && !playRequest.getSound().isLoaded()) {
			return null;
		}

		return sourcePool.play(playRequest);
	}

	/**
	 * Send a request to play a system sound effect at full volume. The request is sent to the {@link SourcePoolManager} which will find a source to play the sound.
	 *
	 * @param sound The sound to be played.
	 *
	 * @return The controller for the playing of this sound.
	 */
	public AudioController playSystemSound(Sound sound) {
		if (sound != null && !sound.isLoaded()) {
			return null;
		}

		return sourcePool.play(PlayRequest.newSystemPlayRequest(sound));
	}

	/**
	 * Gets the sound stream manager.
	 *
	 * @return The sound stream manager.
	 */
	public StreamManager getStreamManager() {
		return streamManager;
	}

	/**
	 * Gets the background music player.
	 *
	 * @return The background music player.
	 */
	public MusicPlayer getMusicPlayer() {
		return musicPlayer;
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
