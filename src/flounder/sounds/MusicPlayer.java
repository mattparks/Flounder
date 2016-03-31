package flounder.sounds;

import java.util.*;

/**
 * The class in charge of playing background music!
 */
public class MusicPlayer {
	private static final float FADE_TIME = 2;

	private float m_musicVolume;

	private SoundSource m_source;
	private Playlist m_currentPlaylist;
	private List<Sound> m_musicQueue;
	private Sound m_currentlyPlaying;

	private boolean m_fadeOut;
	private float m_fadeFactor;
	private float m_finalVolume;

	private boolean m_shuffle; // TODO: Add options for pause after music finishes, min and max length, randomly select time in-between.

	/**
	 * Sets up the sound source that the music player will be using to play sounds, and sets the relevant settings.
	 */
	public MusicPlayer() {
		m_musicVolume = 0.0f;
		m_source = new SoundSource();
		m_source.loop(false);
		m_source.setUndiminishing();
		m_musicQueue = new ArrayList<>();
		m_currentlyPlaying = null;
		m_fadeOut = false;
		m_fadeFactor = 1.0f;
		m_finalVolume = 0.0f;
		m_shuffle = false;
	}

	/**
	 * Updates the fading-out process if a track is currently being faded out. Plays the next track in the queue if the current track has stopped playing.
	 *
	 * @param delta The time in seconds since the last frame.
	 */
	public void update(final float delta) {
		if (m_fadeOut) {
			updateFadeOut(delta);
		}

		if (!m_source.isPlaying() && !m_musicQueue.isEmpty()) {
			m_source.setInactive();
			playNextTrack();
		}
	}

	/**
	 * Updates the fading-out process if a track is currently being faded out. Stops the source from playing the track if the volume has reached 0.
	 *
	 * @param delta Time in seconds since the last frame.
	 */
	private void updateFadeOut(final float delta) {
		m_fadeFactor -= delta / FADE_TIME;
		m_source.setVolume(m_finalVolume * m_fadeFactor);

		if (m_fadeFactor <= 0) {
			m_fadeOut = false;
			m_fadeFactor = 1;
			m_source.stop();
		}
	}

	/**
	 * Plays the next track in the queue and refills the queue with tracks from the current playlist if it's empty.
	 */
	private void playNextTrack() {
		Sound nextTrack = m_musicQueue.remove(0);

		if (m_musicQueue.isEmpty()) {
			fillQueue();
		}

		m_currentlyPlaying = nextTrack;
		m_source.setVolume(m_musicVolume * m_currentlyPlaying.getVolume());
		m_source.playSound(m_currentlyPlaying);
	}

	/**
	 * Fills the queue using tracks from the current playlist. Adds tracks in a random order if shuffle is on.
	 */
	private void fillQueue() {
		if (m_currentPlaylist == null) {
			return;
		}

		if (m_shuffle) {
			m_musicQueue.addAll(m_currentPlaylist.getShuffledMusicList(m_currentlyPlaying));
		} else {
			m_musicQueue.addAll(m_currentPlaylist.getOrderedTracks());
		}
	}

	/**
	 * Sets a new playlist for this music player to play. Any previous playlist's are forgotten, and the currently playing track (if there is one) is faded-out.
	 *
	 * @param playlist The playlist of music to be played in the background.
	 * @param shuffle Whether the playlist should be played in a random order or not.
	 */
	public void playMusicPlaylist(final Playlist playlist, final boolean shuffle) {
		m_shuffle = shuffle;
		m_currentPlaylist = playlist;
		fadeOutCurrentTrack();
		m_musicQueue.clear();
		fillQueue();
	}

	/**
	 * Plays a one-off music track. Whatever track is currently playing is faded out (if desired) and this one-off track is put straight to the front of
	 * the queue so that it gets played as soon as the current track has faded out. This would be used to play music for in-game events, such as an
	 * achievement being reached. Once the track has finished playing the usual background music continues.
	 *
	 * @param music The one-off music track.
	 * @param fadeOutPrevious Whether the currently playing track should be faded out, or just stopped instantly.
	 */
	public void playEventMusic(final Sound music, final boolean fadeOutPrevious) {
		m_musicQueue.add(0, music);

		if (fadeOutPrevious) {
			fadeOutCurrentTrack();
		} else {
			m_source.stop();
		}
	}

	/**
	 * Indicates that the current track should be faded out.
	 */
	private void fadeOutCurrentTrack() {
		if (m_currentlyPlaying != null) {
			m_fadeOut = true;
			m_finalVolume = m_source.getVolume();
		}
	}

	/**
	 * Sets the volume of the music.
	 *
	 * @param volume The new music volume.
	 */
	public void setVolume(final float volume) {
		m_musicVolume = volume;

		if (m_currentlyPlaying != null) {
			m_source.setVolume(m_musicVolume * m_currentlyPlaying.getVolume());
		}
	}

	/**
	 * @return The volume of the music player.
	 */
	public float getVolume() {
		return m_musicVolume;
	}

	/**
	 * @return The currently playing track.
	 */
	public Sound getCurrentlyPlaying() {
		return m_currentlyPlaying;
	}

	/**
	 * Deletes the source being used to play music.
	 */
	public void cleanUp() {
		m_source.delete();
	}
}
