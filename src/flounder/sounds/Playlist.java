package flounder.sounds;

import flounder.maths.*;

import java.util.*;

/**
 * A playlist of music tracks that can be played on the {@link MusicPlayer}.
 */
public class Playlist {
	private final List<Sound> musics;

	/**
	 * Creates a new playlist of music tracks.
	 */
	public Playlist() {
		musics = new ArrayList<>();
	}

	/**
	 * Add a music track to the playlist.
	 *
	 * @param music The track to be added.
	 */
	public void addMusic(final Sound music) {
		musics.add(music);
	}

	/**
	 * Clear all tracks from the playlist.
	 */
	public void clear() {
		musics.clear();
	}

	/**
	 * @return The list of playlist trakcs in their current order.
	 */
	public List<Sound> getOrderedTracks() {
		return musics;
	}

	/**
	 * Returns a list of the tracks in a random order. It ensures that the first track in the list is not the same as the track specified as being
	 * previously played (to avoid a track being repeated twice in a row).
	 *
	 * @param previouslyPlayed The last track to be played.
	 *
	 * @return The list of tracks in a random order.
	 */
	public List<Sound> getShuffledMusicList(final Sound previouslyPlayed) {
		final List<Sound> tempList = new ArrayList<>();
		tempList.addAll(musics);
		final List<Sound> shuffledList = new ArrayList<>();

		while (!tempList.isEmpty()) {
			shuffledList.add(removeRandomTrackFromList(tempList));
		}

		ensurePreviousTrackNotRepeated(shuffledList, previouslyPlayed);
		return shuffledList;
	}

	/**
	 * Gets and removes a random track from a list of tracks.
	 *
	 * @param listOfMusic The list of tracks.
	 *
	 * @return The randomly chosen removed track.
	 */
	private Sound removeRandomTrackFromList(final List<Sound> listOfMusic) {
		final int index = Maths.RANDOM.nextInt(listOfMusic.size());
		return listOfMusic.remove(index);
	}

	/**
	 * Checks if the previously played track is first on the new list, and if it is it gets moved to the back of the list.
	 *
	 * @param newPlaylist The new list.
	 * @param previouslyPlayed The last track to be played.
	 */
	private void ensurePreviousTrackNotRepeated(final List<Sound> newPlaylist, final Sound previouslyPlayed) {
		if (!newPlaylist.isEmpty() && newPlaylist.get(0) == previouslyPlayed) {
			final Sound track = newPlaylist.remove(0);
			newPlaylist.add(track);
		}
	}
}
