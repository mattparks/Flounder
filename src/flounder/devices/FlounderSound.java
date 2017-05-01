package flounder.devices;

import flounder.camera.*;
import flounder.framework.*;
import flounder.logger.*;
import flounder.maths.vectors.*;
import flounder.processing.*;
import flounder.resources.*;
import flounder.sounds.*;

import java.nio.*;

/**
 * A module used for loading, managing and playing a variety of different sound types.
 */
public class FlounderSound extends Module {
	private static final FlounderSound INSTANCE = new FlounderSound();
	public static final String PROFILE_TAB_NAME = "Sound";

	public static final MyFile SOUND_FOLDER = new MyFile(MyFile.RES_FOLDER, "sounds");

	private Vector3f cameraPosition;
	private SourcePoolManager sourcePool;
	private StreamManager streamManager;
	private MusicPlayer musicPlayer;

	private IDeviceSound device;

	/**
	 * Creates a new OpenAL audio manager.
	 */
	public FlounderSound() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME, FlounderProcessors.class);
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

		this.device = null;
	}

	@Handler.Function(Handler.FLAG_UPDATE_PRE)
	public void update() {
		// Gets a new device, if available.
		IDeviceSound newDevice = (IDeviceSound) getExtensionMatch(device, IDeviceSound.class, true);

		// If there is a new player, disable the old one and start to use the new one.
		if (newDevice != null) {
			if (device != null) {
				device.setInitialized(false);
			}

			if (!newDevice.isInitialized()) {
				newDevice.init();
				newDevice.setInitialized(true);
			}

			device = newDevice;
		}

		Camera camera = FlounderCamera.getCamera();

		if (camera != null && camera.getPosition() != null) {
			cameraPosition.set(camera.getPosition());
			musicPlayer.update(Framework.getDelta());
			sourcePool.update();
		}
	}

	@Handler.Function(Handler.FLAG_PROFILE)
	public void profile() {
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
	 * Generates an OpenAL buffer and loads some, if not all, of the sound data into it. The buffer and other information about the audio data gets
	 * set in the sound object. This is called to load a new sound for the first time.
	 *
	 * @param sound The sound to be loaded.
	 */
	public static void doInitialSoundLoad(Sound sound) {
		try {
			FlounderLogger.get().log("Loading sound " + sound.getSoundFile().getPath());
			WavDataStream stream = WavDataStream.openWavStream(sound.getSoundFile(), StreamManager.SOUND_CHUNK_MAX_SIZE);
			sound.setTotalBytes(stream.getTotalBytes());
			ByteBuffer byteBuffer = stream.loadNextData();
			int bufferID = INSTANCE.device.generateBuffer();
			INSTANCE.device.loadSoundDataIntoBuffer(bufferID, byteBuffer, stream.getAlFormat(), stream.getSampleRate());
			sound.setBuffer(bufferID, byteBuffer.limit());
			stream.close();
		} catch (Exception e) {
			FlounderLogger.get().error("Couldn't load sound file " + sound.getSoundFile());
			FlounderLogger.get().exception(e);
		}
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

	/**
	 * Gets the current playform device.
	 *
	 * @return The device.
	 */
	public static IDeviceSound getDevice() {
		return INSTANCE.device;
	}


	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
		streamManager.kill();
		sourcePool.dispose();
		musicPlayer.dispose();
	}
}
