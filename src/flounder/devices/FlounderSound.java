package flounder.devices;

import flounder.camera.*;
import flounder.framework.*;
import flounder.logger.*;
import flounder.maths.vectors.*;
import flounder.platform.*;
import flounder.processing.*;
import flounder.resources.*;
import flounder.sounds.*;

import java.nio.*;

/**
 * A module used for loading, managing and playing a variety of different sound types.
 */
public class FlounderSound extends Module {
	public static final MyFile SOUND_FOLDER = new MyFile(MyFile.RES_FOLDER, "sounds");

	private Vector3f cameraPosition;
	private SourcePoolManager sourcePool;
	private StreamManager streamManager;
	private MusicPlayer musicPlayer;

	/**
	 * Creates a new OpenAL audio manager.
	 */
	public FlounderSound() {
		super(FlounderPlatform.class, FlounderProcessors.class);
	}

	@Module.Instance
	public static FlounderSound get() {
		return (FlounderSound) Framework.get().getInstance(FlounderSound.class);
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
		update();

		this.cameraPosition = new Vector3f();
		this.sourcePool = new SourcePoolManager();
		this.streamManager = new StreamManager();
		streamManager.start();
		this.musicPlayer = new MusicPlayer();
		musicPlayer.setVolume(MusicPlayer.SOUND_VOLUME);
	}

	@Handler.Function(Handler.FLAG_UPDATE_PRE)
	public void update() {
		Camera camera = FlounderCamera.get().getCamera();

		if (camera != null && camera.getPosition() != null) {
			cameraPosition.set(camera.getPosition());
			musicPlayer.update(Framework.get().getDelta());
			sourcePool.update();
		}
	}

	/**
	 * Gets the cameras position.
	 *
	 * @return The cameras position.
	 */
	public Vector3f getCameraPosition() {
		return this.cameraPosition;
	}

	/**
	 * Generates an OpenAL buffer and loads some, if not all, of the sound data into it. The buffer and other information about the audio data gets
	 * set in the sound object. This is called to load a new sound for the first time.
	 *
	 * @param sound The sound to be loaded.
	 */
	public void doInitialSoundLoad(Sound sound) {
		try {
			if (FlounderLogger.DETAILED) {
				FlounderLogger.get().log("Loading sound " + sound.getSoundFile().getPath());
			}

			WavDataStream stream = WavDataStream.openWavStream(sound.getSoundFile(), StreamManager.SOUND_CHUNK_MAX_SIZE);
			sound.setTotalBytes(stream.getTotalBytes());
			ByteBuffer byteBuffer = stream.loadNextData();
			int bufferID = generateBuffer();
			loadSoundDataIntoBuffer(bufferID, byteBuffer, stream.getAlFormat(), stream.getSampleRate());
			sound.setBuffer(bufferID, byteBuffer.limit());
			stream.close();
		} catch (Exception e) {
			FlounderLogger.get().error("Couldn't load sound file " + sound.getSoundFile());
			FlounderLogger.get().exception(e);
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
	@Module.MethodReplace
	public void loadSoundDataIntoBuffer(int bufferID, ByteBuffer data, int format, int sampleRate) {

	}

	/**
	 * Generates an empty sound buffer.
	 *
	 * @return The ID of the buffer.
	 */
	@Module.MethodReplace
	public int generateBuffer() {
		return -1;
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

		return this.sourcePool.play(playRequest);
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

		return this.sourcePool.play(PlayRequest.newSystemPlayRequest(sound));
	}

	public SourcePoolManager getSourcePool() {
		return this.sourcePool;
	}

	/**
	 * Gets the sound stream manager.
	 *
	 * @return The sound stream manager.
	 */
	public StreamManager getStreamManager() {
		return this.streamManager;
	}

	/**
	 * Gets the background music player.
	 *
	 * @return The background music player.
	 */
	public MusicPlayer getMusicPlayer() {
		return this.musicPlayer;
	}

	/**
	 * Creates a new platform specific sound source.
	 *
	 * @return A new sound source.
	 */
	@Module.MethodReplace
	public SoundSource createPlatformSource() {
		return null;
	}

	/**
	 * Determines the OpenAL ID of the sound data format.
	 *
	 * @param channels Number of channels in the audio data.
	 * @param bitsPerSample Number of bits per sample (either 8 or 16).
	 *
	 * @return The OpenAL format ID of the sound data.
	 */
	@Module.MethodReplace
	public int getOpenAlFormat(int channels, int bitsPerSample) {
		return -1;
	}

	/**
	 * Removes a certain sound buffer from memory by removing from the list of buffers and deleting it.
	 *
	 * @param bufferID The ID of the buffer to be deleted.
	 */
	@Module.MethodReplace
	public void deleteBuffer(Integer bufferID) {

	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
		streamManager.kill();
		sourcePool.dispose();
		musicPlayer.dispose();
	}
}
