package flounder.sounds;

import java.util.*;

/**
 * The source pool manager keeps track of all the current sound sources and deals with any requests to play a sound. When a sound needs to be played an
 * unused source is removed from the pool and used to play the sound. When a source finishes playing its current sound it gets added back into the pool.
 */
public class SourcePoolManager {
	private static final int NUMBER_SOURCES = 20;

	private List<SoundSource> sourcePool;
	private List<SoundSource> usedSources;

	private float systemVolume;

	/**
	 * Creates all the sound sources that will ever be used to play sound.
	 */
	public SourcePoolManager() {
		this.sourcePool = new ArrayList<>();
		this.usedSources = new ArrayList<>();

		this.systemVolume = 1.0f;

		for (int i = 0; i < NUMBER_SOURCES; i++) {
			sourcePool.add(new SoundSource());
		}
	}

	/**
	 * Updates all the sources that are currently in use playing sounds and returns any sources that have finished playing their sound to the pool.
	 */
	public void update() {
		Iterator<SoundSource> iterator = usedSources.iterator();

		while (iterator.hasNext()) {
			SoundSource source = iterator.next();

			if (!source.isPlaying()) {
				iterator.remove();
				source.setInactive();
				sourcePool.add(source);
			}
		}
	}

	/**
	 * Plays a sound on an unused sound source, removing it from the pool.
	 *
	 * @param playRequest The sound and information about how to play it.
	 *
	 * @return The sound source being used to play the requested sound. Returns {@code null} if there was no source available.
	 */
	public AudioController play(PlayRequest playRequest) {
		if (!sourcePool.isEmpty()) {
			SoundSource source = sourcePool.remove(0);
			usedSources.add(source);
			source.setPosition(playRequest.getPosition());
			source.loop(playRequest.isLooping());

			if (!playRequest.isSystemSound()) {
				source.setRanges(playRequest.getInnerRange(), playRequest.getOuterRange());
			} else {
				source.setUndiminishing();
			}

			Sound sound = playRequest.getSound();
			source.setVolume(playRequest.getVolume() * sound.getVolume() * systemVolume);
			source.setPitch(playRequest.getPitch() * sound.getPitch());
			return source.playSound(sound);
		}

		return null;
	}

	public float getSystemVolume() {
		return systemVolume;
	}

	public void setSystemVolume(float systemVolume) {
		this.systemVolume = systemVolume;
	}

	/**
	 * Deletes all of the sound sources when the game is closed.
	 */
	public void dispose() {
		sourcePool.forEach(SoundSource::delete);
	}
}
