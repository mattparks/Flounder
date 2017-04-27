package flounder.devices;

import flounder.camera.*;
import flounder.framework.*;
import flounder.logger.*;
import flounder.lwjgl3.sounds.*;
import flounder.maths.vectors.*;
import flounder.processing.*;
import flounder.resources.*;
import flounder.sounds.*;
import org.lwjgl.openal.*;

import java.nio.*;
import java.util.*;

import static org.lwjgl.openal.AL.createCapabilities;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.AL11.*;
import static org.lwjgl.openal.ALC.createCapabilities;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * A module used for loading, managing and playing a variety of different sound types.
 */
public class FlounderSound extends Module {
	private static final FlounderSound INSTANCE = new FlounderSound();
	public static final String PROFILE_TAB_NAME = "Sound";

	public static final MyFile SOUND_FOLDER = new MyFile(MyFile.RES_FOLDER, "sounds");

	private Vector3f cameraPosition;
	private long device;

	private List<Integer> buffers;

	private SourcePoolManager sourcePool;
	private StreamManager streamManager;
	private MusicPlayer musicPlayer;

	/**
	 * Creates a new OpenAL audio manager.
	 */
	public FlounderSound() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME, FlounderProcessors.class);
	}

	@Override
	public void init() {
		this.cameraPosition = new Vector3f();

		// Creates the OpenAL contexts.
		this.device = alcOpenDevice((ByteBuffer) null);
		ALCCapabilities deviceCaps = createCapabilities(device);
		alcMakeContextCurrent(alcCreateContext(device, (IntBuffer) null));
		createCapabilities(deviceCaps);

		this.buffers = new ArrayList<>();

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
		Camera camera = FlounderCamera.getCamera();

		if (camera != null && camera.getPosition() != null) {
			cameraPosition.set(camera.getPosition());
			alListener3f(AL10.AL_POSITION, cameraPosition.x, cameraPosition.y, cameraPosition.z);
			musicPlayer.update(Framework.getDelta());
			sourcePool.update();
		}
	}

	@Override
	public void profile() {
	}

	/**
	 * Creates a new platform specific sound source.
	 *
	 * @return A new sound source.
	 */
	public static SoundSource createPlatformSource() {
		return new LWJGLSoundSource();
	}

	/**
	 * Gets the cameras position.
	 *
	 * @return The cameras position.
	 */
	public static Vector3f getCameraPosition() {
		return INSTANCE.cameraPosition;
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

		return INSTANCE.sourcePool.play(playRequest);
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

		return INSTANCE.sourcePool.play(PlayRequest.newSystemPlayRequest(sound));
	}

	/**
	 * Determines the OpenAL ID of the sound data format.
	 *
	 * @param channels Number of channels in the audio data.
	 * @param bitsPerSample Number of bits per sample (either 8 or 16).
	 *
	 * @return The OpenAL format ID of the sound data.
	 */
	public static int getOpenAlFormat(int channels, int bitsPerSample) {
		if (channels == 1) {
			return bitsPerSample == 8 ? AL_FORMAT_MONO8 : AL_FORMAT_MONO16;
		} else {
			return bitsPerSample == 8 ? AL_FORMAT_STEREO8 : AL_FORMAT_STEREO16;
		}
	}

	/**
	 * Generates an OpenAL buffer and loads some, if not all, of the sound data into it. The buffer and other information about the audio data gets
	 * set in the sound object. This is called to load a new sound for the first time.
	 *
	 * @param sound The sound to be loaded.
	 */
	public static void doInitialSoundLoad(Sound sound) {
		try {
			FlounderLogger.log("Loading sound " + sound.getSoundFile().getPath());
			WavDataStream stream = WavDataStream.openWavStream(sound.getSoundFile(), StreamManager.SOUND_CHUNK_MAX_SIZE);
			sound.setTotalBytes(stream.getTotalBytes());
			ByteBuffer byteBuffer = stream.loadNextData();
			int bufferID = generateBuffer();
			loadSoundDataIntoBuffer(bufferID, byteBuffer, stream.getAlFormat(), stream.getSampleRate());
			sound.setBuffer(bufferID, byteBuffer.limit());
			stream.close();
		} catch (Exception e) {
			FlounderLogger.error("Couldn't load sound file " + sound.getSoundFile());
			FlounderLogger.exception(e);
		}
	}

	/**
	 * Loads audio data of a certain format into an OpenAL buffer.
	 *
	 * @param bufferID The buffer to which the data should be loaded.
	 * @param data The audio data.
	 * @param format The OpenAL format of the data (mono, stereo, etc.)
	 * @param sampleRate The sample rate of the audio.
	 */
	public static void loadSoundDataIntoBuffer(int bufferID, ByteBuffer data, int format, int sampleRate) {
		alBufferData(bufferID, format, data, sampleRate);
		int error = alGetError();

		if (error != AL_NO_ERROR) {
			FlounderLogger.error("Problem loading sound data into buffer. " + error);
		}
	}

	/**
	 * Generates an empty sound buffer.
	 *
	 * @return The ID of the buffer.
	 */
	public static int generateBuffer() {
		int bufferID = alGenBuffers();
		INSTANCE.buffers.add(bufferID);
		return bufferID;
	}

	/**
	 * Removes a certain sound buffer from memory by removing from the list of buffers and deleting it.
	 *
	 * @param bufferID The ID of the buffer to be deleted.
	 */
	public static void deleteBuffer(Integer bufferID) {
		INSTANCE.buffers.remove(bufferID);
		alDeleteBuffers(bufferID);

		if (alGetError() != AL_NO_ERROR) {
			FlounderLogger.warning("Problem deleting sound buffer.");
		}
	}

	public static SourcePoolManager getSourcePool() {
		return INSTANCE.sourcePool;
	}

	/**
	 * Gets the sound stream manager.
	 *
	 * @return The sound stream manager.
	 */
	public static StreamManager getStreamManager() {
		return INSTANCE.streamManager;
	}

	/**
	 * Gets the background music player.
	 *
	 * @return The background music player.
	 */
	public static MusicPlayer getMusicPlayer() {
		return INSTANCE.musicPlayer;
	}

	@Override
	public Module getInstance() {
		return INSTANCE;
	}

	@Override
	public void dispose() {
		streamManager.kill();
		sourcePool.dispose();
		musicPlayer.dispose();

		buffers.forEach(buffer -> {
			if (buffer != null) {
				alDeleteBuffers(buffer);
			}
		});

		if (alGetError() != AL_NO_ERROR) {
			FlounderLogger.warning("Problem deleting sound buffers.");
		}

		alcCloseDevice(device);
	}
}
