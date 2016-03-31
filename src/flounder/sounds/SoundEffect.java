package flounder.sounds;

/**
 * Represents a 3D sound effect which can be played in the 3D world.
 */
public class SoundEffect {
	private final Sound m_sound;
	private final float m_range;

	/**
	 * @param sound The sound used by this sound effect.
	 * @param range The range of the sound effect.
	 */
	public SoundEffect(Sound sound, float range) {
		m_sound = sound;
		m_range = range;
	}

	/**
	 * @return The sound.
	 */
	public Sound getSound() {
		return m_sound;
	}

	/**
	 * @return The range of the sound effect.
	 */
	public float getRange() {
		return m_range;
	}
}
