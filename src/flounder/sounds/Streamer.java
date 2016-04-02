package flounder.sounds;

import flounder.engine.*;

import java.nio.*;
import java.util.*;

/**
 * A streamer object is used to carry out the streaming of one playing of a large audio file. The streamer checks when the source has finished playing
 * buffers and refills them with new data before queueing them up to be played again by the source.
 */
public class Streamer {
	private static final int NUM_BUFFERS = 2;
	private final List<Integer> unusedBuffers;
	private final List<Integer> bufferQueue;
	private SoundSource source;
	private AudioController controller;
	private WavDataStream stream;
	private boolean initialBufferPlaying;

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
	protected Streamer(final Sound sound, final SoundSource source, final AudioController controller) throws Exception {
		Logger.log("Streaming " + sound.getSoundFile().getPath());

		this.source = source;
		this.controller = controller;
		stream = WavDataStream.openWavStream(sound.getSoundFile(), StreamManager.SOUND_CHUNK_MAX_SIZE);
		stream.setStartPoint(sound.getBytesRead());

		initialBufferPlaying = true;

		unusedBuffers = new ArrayList<>();
		bufferQueue = new ArrayList<>();

		for (int i = 0; i < NUM_BUFFERS; i++) {
			unusedBuffers.add(SoundLoader.generateBuffer());
		}
	}

	/**
	 * Checks if there are any buffers which have finished playing and refills them with data.
	 *
	 * @return {@code false} when the source has finished playing the sound and has already removed any buffers from its queue.
	 */
	protected boolean update() {
		if (!controller.isActive()) {
			return false;
		}

		if (!stream.hasEnded() && source.isPlaying()) {
			if (!unusedBuffers.isEmpty()) {
				queueUnusedBuffer();
			} else if (isTopBufferFinished()) {
				refillTopBuffer();
			}
		}

		return controller.isActive();
	}

	/**
	 * Fills the first unused buffer with data and queues it to be played.
	 */
	private void queueUnusedBuffer() {
		final int buffer = unusedBuffers.remove(0);
		loadNextDataIntoBuffer(buffer);
		queueBuffer(buffer);
	}

	/**
	 * Loads the next chunk of audio data into a buffer.
	 *
	 * @param buffer The buffer into which the data should be loaded.
	 */
	private void loadNextDataIntoBuffer(final int buffer) {
		final ByteBuffer data = stream.loadNextData();
		SoundLoader.loadSoundDataIntoBuffer(buffer, data, stream.getAlFormat(), stream.getSampleRate());
	}

	/**
	 * Adds a buffer to the end of the queue to be played by the source.
	 *
	 * @param buffer The buffer to be queued.
	 */
	private void queueBuffer(final int buffer) {
		if (source.isPlaying()) {
			source.queue(buffer);
			bufferQueue.add(buffer);
		}
	}

	/**
	 * @return {@code true} if there is a buffer which the source has already finished playing. This doesn't include the initial sound buffer from the {@link Sound} object, whose data is never changed.
	 */
	private boolean isTopBufferFinished() {
		int finishedBufferCount = source.getFinishedBuffersCount();

		if (finishedBufferCount > 0 && initialBufferPlaying) {
			finishedBufferCount--;
			source.unqueue();
			initialBufferPlaying = false;
		}

		return finishedBufferCount > 0;
	}

	/**
	 * Refills the buffer at the front of the queue with data and re-adds it ton the end of the queue.
	 */
	private void refillTopBuffer() {
		final int topBuffer = unqueueTopBuffer();
		loadNextDataIntoBuffer(topBuffer);
		queueBuffer(topBuffer);
	}

	/**
	 * Removes the top buffer from the queue.
	 *
	 * @return The ID of the top buffer.
	 */
	private int unqueueTopBuffer() {
		final int topBuffer = bufferQueue.remove(0);
		source.unqueue();
		return topBuffer;
	}

	/**
	 * When the streaming of the sound has finished the buffers can be deleted.
	 */
	protected void delete() {
		stream.close();
		bufferQueue.forEach(SoundLoader::deleteBuffer);
		unusedBuffers.forEach(SoundLoader::deleteBuffer);
	}
}
