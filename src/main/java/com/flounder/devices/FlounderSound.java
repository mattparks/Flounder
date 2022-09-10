package com.flounder.devices;

import com.flounder.camera.*;
import com.flounder.framework.*;
import com.flounder.logger.*;
import com.flounder.maths.vectors.*;
import com.flounder.platform.*;
import com.flounder.processing.*;
import com.flounder.resources.*;
import com.flounder.sounds.*;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALCCapabilities;

import java.nio.*;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.AL10.AL_POSITION;
import static org.lwjgl.openal.AL11.AL_LINEAR_DISTANCE_CLAMPED;
import static org.lwjgl.openal.ALC.createCapabilities;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.opengl.GL11.GL_NO_ERROR;

/**
 * A module used for loading, managing and playing a variety of different sound types.
 */
public class FlounderSound extends com.flounder.framework.Module {
	public static final MyFile SOUND_FOLDER = new MyFile(MyFile.RES_FOLDER, "sounds");

	private long device;

	private List<Integer> buffers;

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

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
		// Creates the OpenAL contexts.
		this.device = alcOpenDevice((ByteBuffer) null);
		ALCCapabilities deviceCaps = createCapabilities(device);
		alcMakeContextCurrent(alcCreateContext(device, (IntBuffer) null));
		AL.createCapabilities(deviceCaps);

		this.buffers = new ArrayList<>();

		// Checks for errors.
		int alError = alGetError();

		if (alError != GL_NO_ERROR) {
			FlounderLogger.get().error("OpenAL Error " + alError);
		}

		// Creates a new sound model.
		alDistanceModel(AL_LINEAR_DISTANCE_CLAMPED);

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

		Vector3f cameraPosition = FlounderSound.get().getCameraPosition();

		if (cameraPosition != null) {
			alListener3f(AL_POSITION, cameraPosition.x, cameraPosition.y, cameraPosition.z);
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
	 * Determines the OpenAL ID of the sound data format.
	 *
	 * @param channels Number of channels in the audio data.
	 * @param bitsPerSample Number of bits per sample (either 8 or 16).
	 *
	 * @return The OpenAL format ID of the sound data.
	 */
	public int getOpenAlFormat(int channels, int bitsPerSample) {
		if (channels == 1) {
			return bitsPerSample == 8 ? AL_FORMAT_MONO8 : AL_FORMAT_MONO16;
		} else {
			return bitsPerSample == 8 ? AL_FORMAT_STEREO8 : AL_FORMAT_STEREO16;
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
	public void loadSoundDataIntoBuffer(int bufferID, ByteBuffer data, int format, int sampleRate) {
		alBufferData(bufferID, format, data, sampleRate);
		int error = alGetError();

		if (error != AL_NO_ERROR) {
			FlounderLogger.get().error("Problem loading sound data into buffer. " + error);
		}
	}

	/**
	 * Generates an empty sound buffer.
	 *
	 * @return The ID of the buffer.
	 */
	public int generateBuffer() {
		int bufferID = alGenBuffers();
		this.buffers.add(bufferID);
		return bufferID;
	}

	/**
	 * Removes a certain sound buffer from memory by removing from the list of buffers and deleting it.
	 *
	 * @param bufferID The ID of the buffer to be deleted.
	 */
	public void deleteBuffer(Integer bufferID) {
		this.buffers.remove(bufferID);
		alDeleteBuffers(bufferID);

		if (alGetError() != AL_NO_ERROR) {
			FlounderLogger.get().warning("Problem deleting sound buffer.");
		}
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
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
			FlounderLogger.get().warning("Problem deleting sound buffers.");
		}

		alcCloseDevice(device);
	}

	@com.flounder.framework.Module.Instance
	public static FlounderSound get() {
		return (FlounderSound) Framework.get().getModule(FlounderSound.class);
	}
}
