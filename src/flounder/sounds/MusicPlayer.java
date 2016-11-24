package flounder.sounds;

import flounder.framework.*;
import flounder.maths.*;
import flounder.visual.*;

import java.util.*;

/**
 * The class in charge of playing background music!
 */
public class MusicPlayer {
	public static float FADE_TIME = 1.0f;
	public static float SOUND_VOLUME = 1.0f;

	private SoundSource source;
	private List<Sound> musicQueue;
	private Playlist currentPlaylist;
	private Sound currentlyPlaying;

	private float lastSoundVolume;
	private float volumeMaxMusic;
	private ValueDriver volumeDriver;
	private boolean skippingTrack;

	private boolean shuffle;
	private float minPlayTimeout;
	private float maxPlayTimeout;
	private float selectedTimeout;
	private float timeoutStart;

	private boolean paused;

	/**
	 * Sets up the sound source that the music player will be using to play sounds, and sets the relevant settings.
	 */
	public MusicPlayer() {
		source = new SoundSource();
		source.pause();
		source.loop(false);
		source.setUndiminishing();
		musicQueue = new ArrayList<>();
		currentPlaylist = null;
		currentlyPlaying = null;

		lastSoundVolume = SOUND_VOLUME;
		volumeMaxMusic = 0.0f;
		volumeDriver = new ConstantDriver(volumeMaxMusic * SOUND_VOLUME);
		skippingTrack = false;

		shuffle = false;
		minPlayTimeout = 2.0f;
		maxPlayTimeout = 7.0f;
		selectedTimeout = -1.0f;
		timeoutStart = 0.0f;

		paused = true;
	}

	/**
	 * Updates the fading-out process if a track is currently being faded out. Plays the next track in the queue if the current track has stopped playing.
	 *
	 * @param delta The time in seconds since the last frame.
	 */
	public void update(float delta) {
		if (source == null) {
			return;
		}

		if (lastSoundVolume != SOUND_VOLUME) {
			volumeDriver = new ConstantDriver(volumeMaxMusic * SOUND_VOLUME);
			lastSoundVolume = SOUND_VOLUME;
		}

		// Updates the volume using a driver.
		float volume = volumeDriver.update(delta);
		source.setVolume(volume);

		// Stops music if there is no volume.
		if (volume == 0.0f) {
			// If skipping the track start to play a new source.
			if (skippingTrack) {
				volumeDriver = new SlideDriver(0.0f, volumeMaxMusic, FADE_TIME);
				skippingTrack = false;

				source.stop();
				selectedTimeout = -1.0f;
			} else {
				source.pause();
				paused = true;
			}
		}

		// Select the next track after this one is finished.
		if (!paused && !source.isPlaying() && !musicQueue.isEmpty()) {
			source.setInactive();

			if (timeoutStart == 0.0f) {
				timeoutStart = FlounderFramework.getTimeSec();

				if (selectedTimeout >= 0.0f) {
					selectedTimeout = Maths.randomInRange(minPlayTimeout, maxPlayTimeout);
				}
			}

			if (FlounderFramework.getTimeSec() - timeoutStart > selectedTimeout) {
				timeoutStart = 0.0f;
				selectedTimeout = 0.0f;
				playNextTrack();
			}
		}
	}

	/**
	 * Plays the next track in the queue and refills the queue with tracks from the current playlist if it's empty.
	 */
	private void playNextTrack() {
		Sound nextTrack = musicQueue.remove(0);

		if (musicQueue.isEmpty()) {
			fillQueue();
		}

		currentlyPlaying = nextTrack;
		source.setVolume(volumeMaxMusic * currentlyPlaying.getVolume());
		source.playSound(currentlyPlaying);
	}

	/**
	 * Fills the queue using tracks from the current playlist. Adds tracks in a random order if shuffle is on.
	 */
	private void fillQueue() {
		if (currentPlaylist == null) {
			return;
		}

		if (shuffle) {
			musicQueue.addAll(currentPlaylist.getShuffledMusicList(currentlyPlaying));
		} else {
			musicQueue.addAll(currentPlaylist.getOrderedTracks());
		}
	}

	/**
	 * Sets a new playlist for this music player to play. Any previous playlist's are forgotten, and the currently playing track (if there is one) is faded-out.
	 *
	 * @param playlist The playlist of music to be played in the background.
	 * @param shuffle Whether the playlist should be played in a random order or not.
	 * @param minPlayTimeout The minimum time (in seconds) to pause between shuffling music.
	 * @param maxPlayTimeout The maximum time (in seconds) to pause between shuffling music.
	 */
	public void playMusicPlaylist(Playlist playlist, boolean shuffle, float minPlayTimeout, float maxPlayTimeout) {
		this.shuffle = shuffle;
		this.minPlayTimeout = minPlayTimeout;
		this.maxPlayTimeout = maxPlayTimeout;
		currentPlaylist = playlist;
		musicQueue.clear();
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
	public void playEventMusic(Sound music, boolean fadeOutPrevious) {
		musicQueue.add(0, music);

		if (fadeOutPrevious) {
		} else {
			source.stop();
		}
	}

	/**
	 * Gets if the current music player is paused.
	 *
	 * @return If the current music player is paused.
	 */
	public boolean isPaused() {
		return paused;
	}

	/**
	 * Can pause the current track.
	 */
	public void pauseTrack() {
		if (source == null) {
			return;
		}

		volumeDriver = new SlideDriver(source.getVolume(), 0.0f, FADE_TIME);
	}

	/**
	 * Can unpause the currently paused track.
	 */
	public void unpauseTrack() {
		if (source == null) {
			return;
		}

		source.unpause();
		volumeDriver = new SlideDriver(source.getVolume(), volumeMaxMusic, FADE_TIME);
		paused = false;
	}

	/**
	 * Skips the track that is currently playing.
	 */
	public void skipTrack() {
		if (source == null) {
			return;
		}

		volumeDriver = new SlideDriver(source.getVolume(), 0.0f, FADE_TIME);
		skippingTrack = true;
	}

	/**
	 * @return The volume of the music player.
	 */
	public float getVolume() {
		return volumeMaxMusic;
	}

	/**
	 * Sets the volume of the music.
	 *
	 * @param volume The new music volume.
	 */
	public void setVolume(float volume) {
		if (volumeMaxMusic != volume) {
			volumeMaxMusic = volume;
			volumeDriver = new ConstantDriver(volumeMaxMusic * SOUND_VOLUME);
		}
	}

	/**
	 * Deletes the source being used to play music.
	 */
	public void cleanUp() {
		if (source != null) {
			source.delete();
		}
	}
}