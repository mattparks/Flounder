package flounder.sounds;

import flounder.engine.*;
import flounder.maths.*;

import java.util.*;

/**
 * The class in charge of playing background music!
 */
public class MusicPlayer {
	private static final float FADE_TIME = 2;

	private float musicVolume;

	private SoundSource source;
	private Playlist currentPlaylist;
	private List<Sound> musicQueue;
	private Sound currentlyPlaying;
	private float selectedTimeout;
	private float timoutStart;

	private boolean fadeOut;
	private float fadeFactor;
	private float finalVolume;

	private boolean shuffle;
	private float minPlayTimeout;
	private float maxPlayTimeout;

	/**
	 * Sets up the sound source that the music player will be using to play sounds, and sets the relevant settings.
	 */
	public MusicPlayer() {
		musicVolume = 0.0f;
		source = new SoundSource();
		source.loop(false);
		source.setUndiminishing();
		musicQueue = new ArrayList<>();
		currentlyPlaying = null;
		selectedTimeout = 0.0f;
		timoutStart = 0.0f;
		fadeOut = false;
		fadeFactor = 1.0f;
		finalVolume = 0.0f;
		shuffle = false;
		minPlayTimeout = 2.0f;
		maxPlayTimeout = 7.0f;
	}

	/**
	 * Updates the fading-out process if a track is currently being faded out. Plays the next track in the queue if the current track has stopped playing.
	 *
	 * @param delta The time in seconds since the last frame.
	 */
	public void update(final float delta) {
		if (fadeOut) {
			updateFadeOut(delta);
		}

		if (!source.isPlaying() && !musicQueue.isEmpty()) {
			source.setInactive();

			if (timoutStart == 0.0f) {
				timoutStart = FlounderEngine.getTime();
				selectedTimeout = Maths.randomInRange(minPlayTimeout, maxPlayTimeout);
			}

			if (FlounderEngine.getTime() - timoutStart > selectedTimeout) {
				timoutStart = 0.0f;
				selectedTimeout = 0.0f;
				playNextTrack();
			}
		}
	}

	/**
	 * Updates the fading-out process if a track is currently being faded out. Stops the source from playing the track if the volume has reached 0.
	 *
	 * @param delta Time in seconds since the last frame.
	 */
	private void updateFadeOut(final float delta) {
		fadeFactor -= delta / FADE_TIME;
		source.setVolume(finalVolume * fadeFactor);

		if (fadeFactor <= 0) {
			fadeOut = false;
			fadeFactor = 1;
			source.stop();
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
		source.setVolume(musicVolume * currentlyPlaying.getVolume());
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
	public void playMusicPlaylist(final Playlist playlist, final boolean shuffle, final float minPlayTimeout, final float maxPlayTimeout) {
		this.shuffle = shuffle;
		this.minPlayTimeout = minPlayTimeout;
		this.maxPlayTimeout = maxPlayTimeout;
		currentPlaylist = playlist;
		fadeOutCurrentTrack();
		musicQueue.clear();
		fillQueue();
	}

	/**
	 * Indicates that the current track should be faded out.
	 */
	private void fadeOutCurrentTrack() {
		if (currentlyPlaying != null) {
			fadeOut = true;
			finalVolume = source.getVolume();
		}
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
		musicQueue.add(0, music);

		if (fadeOutPrevious) {
			fadeOutCurrentTrack();
		} else {
			source.stop();
		}
	}

	/**
	 * @return The volume of the music player.
	 */
	public float getVolume() {
		return musicVolume;
	}

	/**
	 * Sets the volume of the music.
	 *
	 * @param volume The new music volume.
	 */
	public void setVolume(final float volume) {
		musicVolume = volume;

		if (currentlyPlaying != null) {
			source.setVolume(musicVolume * currentlyPlaying.getVolume());
		}
	}

	/**
	 * @return The currently playing track.
	 */
	public Sound getCurrentlyPlaying() {
		return currentlyPlaying;
	}

	/**
	 * Deletes the source being used to play music.
	 */
	public void cleanUp() {
		source.delete();
	}
}
