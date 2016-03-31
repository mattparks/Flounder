package flounder.sounds;

import flounder.processing.*;
import flounder.processing.glProcessing.*;
import flounder.resources.*;

/**
 * Represents a single sound effect. Holds a reference to the sound's file, and a reference to any buffers containing loaded data of the sound.
 */
public class Sound {
	private int m_bufferID;
	private MyFile m_file;
	private float m_volume;
	private boolean m_loaded;

	private int m_totalBytes;
	private int m_bytesRead;

	/**
	 * Creates a new sound. Note the sound hasn't been loaded at this stage.
	 *
	 * @param soundFile The sound's file.
	 * @param volume Used to change the base volume of a sound effect.
	 */
	private Sound(final MyFile soundFile, final float volume) {
		m_file = soundFile;
		m_volume = 1.0f;
		m_loaded = false;
		m_volume = volume;
	}

	/**
	 * Loads a sound file and loads some (possibly all for short sounds) of the sound data into an OpenAL buffer.
	 *
	 * @param soundFile The sound file.
	 * @param volume Used to change the base volume of a sound effect.
	 *
	 * @return A new sound object which represents the loaded sound.
	 */
	public static final Sound loadSoundNow(final MyFile soundFile, final float volume) {
		Sound sound = new Sound(soundFile, volume);
		SoundLoader.doInitialSoundLoad(sound);
		return sound;
	}

	/**
	 * Sends a request to the resource loading thread for the sound to be loaded.
	 *
	 * @param soundFile The sound file.
	 * @param volume Used to change the base volume of a sound effect.
	 *
	 * @return A new sound object which represents the loaded sound.
	 */
	public static final Sound loadSoundInBackground(final MyFile soundFile, final float volume) {
		final Sound sound = new Sound(soundFile, volume);
		RequestProcessor.sendRequest(() -> SoundLoader.doInitialSoundLoad(sound));
		return sound;
	}

	/**
	 * Sends a request for the sound to be deleted.
	 */
	public void delete() {
		if (m_loaded) {
			GlRequestProcessor.sendRequest(() -> SoundLoader.deleteBuffer(m_bufferID));
			m_loaded = false;
		}
	}

	/**
	 * @return The ID of the sound's buffer.
	 */
	public final int getBufferID() {
		return m_bufferID;
	}

	/**
	 * Sets the buffer containing (at least some of) the sound's audio data. It also indicates how many bytes of the sound's data has been loaded into the buffer.
	 *
	 * @param buffer The ID of the OpenAL buffer which contains the sound's loaded audio data.
	 * @param bytesRead The number of bytes of the sound's audio which have been loaded into the buffer.
	 */
	public void setBuffer(final int buffer, final int bytesRead) {
		m_bufferID = buffer;
		m_bytesRead = bytesRead;
		m_loaded = true;
	}

	/**
	 * @return The sound file.
	 */
	public final MyFile getSoundFile() {
		return m_file;
	}

	/**
	 * @return The base volume of the sound.
	 */
	public final float getVolume() {
		return m_volume;
	}

	/**
	 * @return Whether the sound is loaded or not.
	 */
	public final boolean isLoaded() {
		return m_loaded;
	}

	/**
	 * When initially loading a sound only a certain number of bytes are read. If the sound file for this sound contains more bytes than the number of
	 * bytes which were initially loaded then the sound file needs to be streamed when played.
	 *
	 * @return {@code true} if the sound file needs streaming when it's played.
	 */
	public final boolean needsStreaming() {
		return m_bytesRead < m_totalBytes;
	}

	/**
	 * @return The number of bytes from the sound's file that have already been loaded.
	 */
	public final int getBytesRead() {
		return m_bytesRead;
	}

	/**
	 * Sets the total number of bytes of audio data in the sound's file.
	 *
	 * @param totalBytes The total number of bytes of data.
	 */
	public void setTotalBytes(final int totalBytes) {
		m_totalBytes = totalBytes;
	}
}
