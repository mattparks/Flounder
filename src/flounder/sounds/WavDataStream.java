package flounder.sounds;

import flounder.resources.*;
import org.lwjgl.*;
import org.lwjgl.openal.*;

import javax.sound.sampled.*;
import java.io.*;
import java.nio.*;

/**
 * Class that enables streaming wav data.
 */
public class WavDataStream {
	private final int m_alFormat;
	private final int m_sampleRate;
	private final int m_totalBytes;
	private final int m_bytesPerFrame;

	private final int m_chunkSize;
	private final AudioInputStream m_audioStream;

	private final ByteBuffer m_buffer;
	private final byte[] m_data;

	private int m_totalBytesRead = 0;

	/**
	 * Creates a new wav data streamer.
	 *
	 * @param stream The audio input stream.
	 * @param chunkSize The size of the chunks to read.
	 */
	private WavDataStream(final AudioInputStream stream, final int chunkSize) {
		AudioFormat format = stream.getFormat();
		m_audioStream = stream;
		m_chunkSize = chunkSize;
		m_alFormat = getOpenAlFormat(format.getChannels(), format.getSampleSizeInBits());
		m_buffer = BufferUtils.createByteBuffer(chunkSize);
		m_data = new byte[chunkSize];
		m_sampleRate = (int) format.getSampleRate();
		m_bytesPerFrame = format.getFrameSize();
		m_totalBytes = (int) (stream.getFrameLength() * m_bytesPerFrame);
	}

	/**
	 * Sets the point to start at.
	 *
	 * @param bytesRead Total bytes read.
	 */
	protected void setStartPoint(int bytesRead) {
		m_totalBytesRead = bytesRead;

		try {
			// Why can't I use audioStream.skip(bytesRead)?? Surely that should work, but doesn't :(
			m_audioStream.read(m_data, 0, bytesRead);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads the next chunk of data from the .wav file into a ByteBuffer. The amount of bytes that it attempts to load is determined by the
	 * {@code chunkSize} argument when the {@link #openWavStream(MyFile, int) openWavStream()} method was called to create this stream.
	 * The actual number of bytes loaded may be less depending on how close to the end of the stream it is, or if the {@code chunkSize} doesn't represent an integer number of audio frames.
	 *
	 * @return The loaded byte buffer.
	 */
	protected final ByteBuffer loadNextData() {
		try {
			int bytesRead = m_audioStream.read(m_data, 0, m_chunkSize);
			m_totalBytesRead += bytesRead;
			m_buffer.clear();
			m_buffer.put(m_data, 0, bytesRead);
			m_buffer.flip();
		} catch (IOException e) {
			System.err.println("Couldn't read more bytes from audio stream!");
			e.printStackTrace();
		}
		return m_buffer;
	}

	/**
	 * @return {@code true} if the stream has read all the audio data and reached the end of the data.
	 */
	protected final boolean hasEnded() {
		return m_totalBytesRead >= m_totalBytes;
	}

	public int getAlFormat() {
		return m_alFormat;
	}

	public int getSampleRate() {
		return m_sampleRate;
	}

	public int getTotalBytes() {
		return m_totalBytes;
	}

	public int getBytesPerFrame() {
		return m_bytesPerFrame;
	}

	/**
	 * Closes the stream.
	 */
	protected void close() {
		try {
			m_audioStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a new open wave data stream which can be used to stream audio data from the chosen .wav file.
	 *
	 * @param wavFile The file to be streamed.
	 * @param chunkSize The maximum amount of data in bytes that should be read from the file each time {@link #loadNextData()} is called.
	 *
	 * @return The open wave data stream.
	 *
	 * @throws Exception If something goes wrong.
	 */
	protected static final WavDataStream openWavStream(final MyFile wavFile, final int chunkSize) throws Exception {
		InputStream bufferedInput = new BufferedInputStream(wavFile.getInputStream());
		AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedInput);
		WavDataStream wavStream = new WavDataStream(audioStream, chunkSize);
		return wavStream;
	}

	/**
	 * Determines the OpenAL ID of the sound data format.
	 *
	 * @param channels Number of channels in the audio data.
	 * @param bitsPerSample Number of bits per sample (either 8 or 16).
	 *
	 * @return The OpenAL format ID of the sound data.
	 */
	private static final int getOpenAlFormat(final int channels, final int bitsPerSample) {
		if (channels == 1) {
			return bitsPerSample == 8 ? AL10.AL_FORMAT_MONO8 : AL10.AL_FORMAT_MONO16;
		} else {
			return bitsPerSample == 8 ? AL10.AL_FORMAT_STEREO8 : AL10.AL_FORMAT_STEREO16;
		}
	}
}
