package flounder.sounds;

import flounder.engine.*;

import java.util.*;

/**
 * This thread runs in the background and keeps any audio streams updated. It continuously loops through all currently
 * active {@link Streamer}s and updates them, removing and deleting any which have finished their streaming duty.
 */
public class StreamManager extends Thread {
	public static final int SOUND_CHUNK_MAX_SIZE = 100000;
	public static final long SLEEP_TIME = 100;
	public static final StreamManager STREAMER = new StreamManager();

	private final List<Streamer> streamers;
	private final List<Streamer> toRemove;
	private boolean alive;

	/**
	 * Creates a new object that updates audio streams in a separate thread.
	 */
	public StreamManager() {
		streamers = new ArrayList<>();
		toRemove = new ArrayList<>();
		alive = true;
	}

	@Override
	public void run() {
		while (alive) {
			final List<Streamer> safeCopy = new ArrayList<>(streamers);
			safeCopy.forEach(this::updateStreamer);
			removeFinishedStreamers();
			pause();
		}
	}

	/**
	 * Removes any finished {@link Streamer}s from the list of current streamers, and deletes them (deletes their buffers).
	 */
	private synchronized void removeFinishedStreamers() {
		for (Streamer streamer : toRemove) {
			streamers.remove(streamer);
			streamer.delete();
		}

		toRemove.clear();
	}

	/**
	 * Makes the thread sleep for a while so that it doesn't take up 100% CPU.
	 */
	private void pause() {
		try {
			Thread.sleep(SLEEP_TIME);
		} catch (InterruptedException e) {
			FlounderLogger.error("Thread could not sleep!");
			FlounderLogger.exception(e);
		}
	}

	/**
	 * Updates a streamer and checks whether it has finished streaming. If so it indicates that it should be removed from the list of current streamers.
	 *
	 * @param streamer The streamer to be updated.
	 */
	private void updateStreamer(final Streamer streamer) {
		if (!streamer.update()) {
			toRemove.add(streamer);
		}
	}

	/**
	 * Stops the thread from running.
	 */
	public void kill() {
		alive = false;
	}

	/**
	 * Sets up a new {@link Streamer} to stream a sound file.
	 *
	 * @param sound The sound to be streamed.
	 * @param source The source which will play the sound while it is streamed.
	 * @param controller The controller which can be used to find out when the source
	 * has finished playing the sound in question.
	 */
	protected synchronized void stream(final Sound sound, final SoundSource source, final AudioController controller) {
		try {
			streamers.add(new Streamer(sound, source, controller));
		} catch (Exception e) {
			FlounderLogger.error("Couldn't open stream for sound " + sound.getSoundFile().getPath());
			FlounderLogger.exception(e);
		}
	}
}
