package com.flounder.sounds;

import com.flounder.devices.*;
import com.flounder.logger.*;
import com.flounder.platform.*;
import com.flounder.resources.*;

import javax.sound.sampled.*;
import java.io.*;
import java.nio.*;

/**
 * Class that enables streaming wav data.
 */
public class WavDataStream {
	private int alFormat;
	private int sampleRate;
	private int totalBytes;
	private int bytesPerFrame;

	private int chunkSize;
	private AudioInputStream audioStream;

	private ByteBuffer buffer;
	private byte[] data;

	private int totalBytesRead;

	/**
	 * Creates a new wav data streamer.
	 *
	 * @param stream The audio input stream.
	 * @param chunkSize The size of the chunks to read.
	 */
	private WavDataStream(AudioInputStream stream, int chunkSize) {
		AudioFormat format = stream.getFormat();

		alFormat = FlounderSound.get().getOpenAlFormat(format.getChannels(), format.getSampleSizeInBits());
		sampleRate = (int) format.getSampleRate();
		totalBytes = (int) (stream.getFrameLength() * format.getFrameSize());
		bytesPerFrame = format.getFrameSize();

		this.chunkSize = chunkSize;
		audioStream = stream;

		buffer = FlounderPlatform.get().createByteBuffer(chunkSize);
		data = new byte[chunkSize];

		totalBytesRead = 0;
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
	public static WavDataStream openWavStream(MyFile wavFile, int chunkSize) throws Exception {
		InputStream bufferedInput = new BufferedInputStream(wavFile.getInputStream());
		AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedInput);
		WavDataStream wavStream = new WavDataStream(audioStream, chunkSize);
		return wavStream;
	}

	/**
	 * Sets the point to start at.
	 *
	 * @param bytesRead Total bytes read.
	 */
	protected void setStartPoint(int bytesRead) {
		totalBytesRead = bytesRead;

		try {
			audioStream.read(data, 0, bytesRead);
		} catch (IOException e) {
			FlounderLogger.get().error("Could not set Wav Data Stream start point!");
			FlounderLogger.get().exception(e);
		}
	}

	/**
	 * Loads the next chunk of data from the .wav file into a ByteBuffer. The amount of bytes that it attempts to load is determined by the
	 * {@code chunkSize} argument when the {@link #openWavStream(MyFile, int) openWavStream()} method was called to create this stream.
	 * The actual number of bytes loaded may be less depending on how close to the end of the stream it is, or if the {@code chunkSize} doesn't represent an integer number of audio frames.
	 *
	 * @return The loaded byte buffer.
	 */
	public ByteBuffer loadNextData() {
		try {
			int bytesRead = audioStream.read(data, 0, chunkSize);
			totalBytesRead += bytesRead;
			buffer.clear();
			buffer.put(data, 0, bytesRead);
			buffer.flip();
		} catch (IOException e) {
			FlounderLogger.get().error("Couldn't read more bytes from audio stream!");
			FlounderLogger.get().exception(e);
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
	public void close() {
		try {
			audioStream.close();
		} catch (IOException e) {
			FlounderLogger.get().error("Could not close Wav Data Streamer!");
			FlounderLogger.get().exception(e);
		}
	}
}
