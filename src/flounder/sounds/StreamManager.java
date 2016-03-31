package flounder.sounds;

import java.util.*;

/**
 * This thread runs in the background and keeps any audio streams updated. It continuously loops through all currently
 * active {@link Streamer}s and updates them, removing and deleting any which have finished their streaming duty.
 */
public class StreamManager extends Thread {
	public static final int SOUND_CHUNK_MAX_SIZE = 100000;
	public static final long SLEEP_TIME = 100;

	public static final StreamManager STREAMER = new StreamManager();

	private final List<Streamer> m_streamers;
	private final List<Streamer> m_toRemove;
	private boolean m_alive;

	/**
	 * Creates a new object that updates audio streams in a separate thread.
	 */
	public StreamManager() {
		m_streamers = new ArrayList<>();
		m_toRemove = new ArrayList<>();
		m_alive = true;
	}

	@Override
	public void run() {
		while (m_alive) {
			List<Streamer> safeCopy = new ArrayList<>(m_streamers);
			safeCopy.forEach(streamer -> updateStreamer(streamer));
			removeFinishedStreamers();
			pause();
		}
	}

	/**
	 * Stops the thread from running.
	 */
	public void kill() {
		m_alive = false;
	}

	/**
	 * Updates a streamer and checks whether it has finished streaming. If so it indicates that it should be removed from the list of current streamers.
	 *
	 * @param streamer The streamer to be updated.
	 */
	private void updateStreamer(Streamer streamer) {
		boolean stillAlive = streamer.update();

		if (!stillAlive) {
			m_toRemove.add(streamer);
		}
	}

	/**
	 * Removes any finished {@link Streamer}s from the list of current streamers, and deletes them (deletes their buffers).
	 */
	private synchronized void removeFinishedStreamers() {
		for (Streamer streamer : m_toRemove) {
			m_streamers.remove(streamer);
			streamer.delete();
		}

		m_toRemove.clear();
	}

	/**
	 * Makes the thread sleep for a while so that it doesn't take up 100% CPU.
	 */
	private void pause() {
		try {
			Thread.sleep(SLEEP_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets up a new {@link Streamer} to stream a sound file.
	 *
	 * @param sound The sound to be streamed.
	 * @param source The source which will play the sound while it is streamed.
	 * @param controller The controller which can be used to find out when the source
	 * has finished playing the sound in question.
	 */
	protected synchronized void stream(Sound sound, SoundSource source, AudioController controller) {
		try {
			m_streamers.add(new Streamer(sound, source, controller));
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Couldn't open stream for sound " + sound.getSoundFile());
		}
	}
}
