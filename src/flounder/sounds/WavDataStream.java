package flounder.sounds;

import flounder.engine.*;
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
	private final int alFormat;
	private final int sampleRate;
	private final int totalBytes;
	private final int bytesPerFrame;

	private final int chunkSize;
	private final AudioInputStream audioStream;

	private final ByteBuffer buffer;
	private final byte[] data;

	private int totalBytesRead;

	/**
	 * Creates a new wav data streamer.
	 *
	 * @param stream The audio input stream.
	 * @param chunkSize The size of the chunks to read.
	 */
	private WavDataStream(final AudioInputStream stream, final int chunkSize) {
		final AudioFormat format = stream.getFormat();

		alFormat = getOpenAlFormat(format.getChannels(), format.getSampleSizeInBits());
		sampleRate = (int) format.getSampleRate();
		totalBytes = (int) (stream.getFrameLength() * format.getFrameSize());
		bytesPerFrame = format.getFrameSize();

		this.chunkSize = chunkSize;
		audioStream = stream;

		buffer = BufferUtils.createByteBuffer(chunkSize);
		data = new byte[chunkSize];

		totalBytesRead = 0;
	}

	/**
	 * Determines the OpenAL ID of the sound data format.
	 *
	 * @param channels Number of channels in the audio data.
	 * @param bitsPerSample Number of bits per sample (either 8 or 16).
	 *
	 * @return The OpenAL format ID of the sound data.
	 */
	private static int getOpenAlFormat(final int channels, final int bitsPerSample) {
		if (channels == 1) {
			return bitsPerSample == 8 ? AL10.AL_FORMAT_MONO8 : AL10.AL_FORMAT_MONO16;
		} else {
			return bitsPerSample == 8 ? AL10.AL_FORMAT_STEREO8 : AL10.AL_FORMAT_STEREO16;
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
	protected static WavDataStream openWavStream(final MyFile wavFile, final int chunkSize) throws Exception {
		final InputStream bufferedInput = new BufferedInputStream(wavFile.getInputStream());
		final AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedInput);
		final WavDataStream wavStream = new WavDataStream(audioStream, chunkSize);
		return wavStream;
	}

	/**
	 * Sets the point to start at.
	 *
	 * @param bytesRead Total bytes read.
	 */
	protected void setStartPoint(final int bytesRead) {
		totalBytesRead = bytesRead;

		try {
			audioStream.read(data, 0, bytesRead);
		} catch (IOException e) {
			FlounderLogger.error("Could not set Wav Data Stream start point!");
			FlounderLogger.exception(e);
		}
	}

	/**
	 * Loads the next chunk of data from the .wav file into a ByteBuffer. The amount of bytes that it attempts to load is determined by the
	 * {@code chunkSize} argument when the {@link #openWavStream(MyFile, int) openWavStream()} method was called to create this stream.
	 * The actual number of bytes loaded may be less depending on how close to the end of the stream it is, or if the {@code chunkSize} doesn't represent an integer number of audio frames.
	 *
	 * @return The loaded byte buffer.
	 */
	protected ByteBuffer loadNextData() {
		try {
			final int bytesRead = audioStream.read(data, 0, chunkSize);
			totalBytesRead += bytesRead;
			buffer.clear();
			buffer.put(data, 0, bytesRead);
			buffer.flip();
		} catch (IOException e) {
			FlounderLogger.error("Couldn't read more bytes from audio stream!");
			FlounderLogger.exception(e);
		}
		return buffer;
	}

	/**
	 * @return {@code true} if the stream has read all the audio data and reached the end of the data.
	 */
	protected boolean hasEnded() {
		return totalBytesRead >= totalBytes;
	}

	public int getAlFormat() {
		return alFormat;
	}

	public int getSampleRate() {
		return sampleRate;
	}

	public int getTotalBytes() {
		return totalBytes;
	}

	public int getBytesPerFrame() {
		return bytesPerFrame;
	}

	/**
	 * Closes the stream.
	 */
	protected void close() {
		try {
			audioStream.close();
		} catch (IOException e) {
			FlounderLogger.error("Could not close Wav Data Streamer!");
			FlounderLogger.exception(e);
		}
	}
}
