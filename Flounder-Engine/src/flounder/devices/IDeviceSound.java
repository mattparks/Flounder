package flounder.devices;

import flounder.framework.*;
import flounder.sounds.*;

import java.nio.*;

public abstract class IDeviceSound extends Extension  {
	public IDeviceSound(Class... requires) {
		super(FlounderSound.class, requires);
	}

	public abstract void init();

	public abstract void update();

	/**
	 * Creates a new platform specific sound source.
	 *
	 * @return A new sound source.
	 */
	public abstract SoundSource createPlatformSource();

	/**
	 * Determines the OpenAL ID of the sound data format.
	 *
	 * @param channels Number of channels in the audio data.
	 * @param bitsPerSample Number of bits per sample (either 8 or 16).
	 *
	 * @return The OpenAL format ID of the sound data.
	 */
	public abstract int getOpenAlFormat(int channels, int bitsPerSample);

	/**
	 * Loads audio data of a certain format into an OpenAL buffer.
	 *
	 * @param bufferID The buffer to which the data should be loaded.
	 * @param data The audio data.
	 * @param format The OpenAL format of the data (mono, stereo, etc.)
	 * @param sampleRate The sample rate of the audio.
	 */
	public abstract void loadSoundDataIntoBuffer(int bufferID, ByteBuffer data, int format, int sampleRate);

	/**
	 * Generates an empty sound buffer.
	 *
	 * @return The ID of the buffer.
	 */
	public abstract int generateBuffer();

	/**
	 * Removes a certain sound buffer from memory by removing from the list of buffers and deleting it.
	 *
	 * @param bufferID The ID of the buffer to be deleted.
	 */
	public abstract void deleteBuffer(Integer bufferID);

	public abstract void dispose();

	@Override
	public boolean isActive() {
		return true;
	}
}
