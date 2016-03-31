package flounder.sounds;

import java.nio.*;
import java.util.*;

/**
 * A streamer object is used to carry out the streaming of one playing of a large audio file. The streamer checks when the source has finished playing
 * buffers and refills them with new data before queueing them up to be played again by the source.
 */
public class Streamer {
	private static final int NUM_BUFFERS = 2;

	private SoundSource m_source;
	private AudioController m_controller;
	private WavDataStream m_stream;

	private boolean m_initialBufferPlaying;

	private List<Integer> m_unusedBuffers;
	private List<Integer> m_bufferQueue;

	/**
	 * Create a new stream to play a certain sound using a certain sound source.
	 * This also opens the data input stream for the sound file and creates the buffers which will be used to hold chunks of audio data.
	 *
	 * @param sound The sound to be streamed.
	 * @param source The source being used to play the sound.
	 * @param controller The controller which indicates when the source has stopped playing the sound.
	 *
	 * @throws Exception When something goes wrong :(
	 */
	protected Streamer(Sound sound, SoundSource source, AudioController controller) throws Exception {
		System.out.println("Streaming " + sound.getSoundFile());
		m_source = source;
		m_controller = controller;
		m_stream = WavDataStream.openWavStream(sound.getSoundFile(), StreamManager.SOUND_CHUNK_MAX_SIZE);
		m_stream.setStartPoint(sound.getBytesRead());
		m_initialBufferPlaying = true;
		m_unusedBuffers = new ArrayList<>();
		m_bufferQueue = new ArrayList<>();

		for (int i = 0; i < NUM_BUFFERS; i++) {
			m_unusedBuffers.add(SoundLoader.generateBuffer());
		}
	}

	/**
	 * Checks if there are any buffers which have finished playing and refills them with data.
	 *
	 * @return {@code false} when the source has finished playing the sound and has already removed any buffers from its queue.
	 */
	protected boolean update() {
		if (!m_controller.isActive()) {
			return false;
		}

		if (!m_stream.hasEnded() && m_source.isPlaying()) {
			if (!m_unusedBuffers.isEmpty()) {
				queueUnusedBuffer();
			} else if (isTopBufferFinished()) {
				refillTopBuffer();
			}
		}

		return m_controller.isActive();
	}

	/**
	 * Fills the first unused buffer with data and queues it to be played.
	 */
	private void queueUnusedBuffer() {
		int buffer = m_unusedBuffers.remove(0);
		loadNextDataIntoBuffer(buffer);
		queueBuffer(buffer);
	}

	/**
	 * @return {@code true} if there is a buffer which the source has already finished playing. This doesn't include the initial sound buffer from the {@link Sound} object, whose data is never changed.
	 */
	private boolean isTopBufferFinished() {
		int finishedBufferCount = m_source.getFinishedBuffersCount();

		if (finishedBufferCount > 0 && m_initialBufferPlaying) {
			finishedBufferCount--;
			m_source.unqueue();
			m_initialBufferPlaying = false;
		}

		return finishedBufferCount > 0;
	}

	/**
	 * Refills the buffer at the front of the queue with data and re-adds it ton the end of the queue.
	 */
	private void refillTopBuffer() {
		int topBuffer = unqueueTopBuffer();
		loadNextDataIntoBuffer(topBuffer);
		queueBuffer(topBuffer);
	}

	/**
	 * Loads the next chunk of audio data into a buffer.
	 *
	 * @param buffer The buffer into which the data should be loaded.
	 */
	private void loadNextDataIntoBuffer(int buffer) {
		ByteBuffer data = m_stream.loadNextData();
		SoundLoader.loadSoundDataIntoBuffer(buffer, data, m_stream.getAlFormat(), m_stream.getSampleRate());
	}

	/**
	 * Adds a buffer to the end of the queue to be played by the source.
	 *
	 * @param buffer The buffer to be queued.
	 */
	private void queueBuffer(int buffer) {
		if (m_source.isPlaying()) {
			m_source.queue(buffer);
			m_bufferQueue.add(buffer);
		}
	}

	/**
	 * Removes the top buffer from the queue.
	 *
	 * @return The ID of the top buffer.
	 */
	private int unqueueTopBuffer() {
		int topBuffer = m_bufferQueue.remove(0);
		m_source.unqueue();
		return topBuffer;
	}

	/**
	 * When the streaming of the sound has finished the buffers can be deleted.
	 */
	protected void delete() {
		m_stream.close();

		for (Integer buffer : m_bufferQueue) {
			SoundLoader.deleteBuffer(buffer);
		}

		for (Integer buffer : m_unusedBuffers) {
			SoundLoader.deleteBuffer(buffer);
		}
	}
}
