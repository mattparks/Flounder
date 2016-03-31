package flounder.sounds;

import flounder.maths.vectors.*;

/**
 * An object which allows other classes to alter the settings of a sound source while playing a certain sound. Whenever a play request is made to the {@link SoundSource}
 * an AudioController is returned allowing the source's settings to be updated while the sound is playing. Once the sound has finished playing the controller becomes inactive.
 */
public class AudioController {
	private static final float FADE_TIME = 2;

	private SoundSource m_source;
	private boolean m_active;
	private boolean m_fading;

	private float m_finalVolume;
	private float m_fadeFactor;

	/**
	 * Creates a new controller for the given sound source.
	 *
	 * @param source The sound source that this controller can control.
	 */
	protected AudioController(SoundSource source) {
		m_source = source;
		m_active = true;
		m_fading = false;
		m_finalVolume = 0.0f;
		m_fadeFactor = 1.0f;
	}

	/**
	 * Updates the controller, checking whether the controller is still active.
	 *
	 * @param delta The time in seconds since the last frame.
	 *
	 * @return {@code true} if the controller is still active. If the source has stopped playing the sound that this controller was created for
	 * then the controller is no longer active and will return {@code false}.
	 */
	protected boolean update(final float delta) {
		if (m_active) {
			updateActiveController(delta);
		}

		return m_active;
	}

	/**
	 * If necessary it continues fading out the volume of the source.
	 *
	 * @param delta Time in seconds since the last frame.
	 */
	private void updateActiveController(float delta) {
		if (m_fading) {
			updateFadingOut(delta);
		}
	}

	/**
	 * Fades out the volume of the source over time. Once the volume reaches 0 the source playing the sound is stopped (rendering this controller inactive).
	 *
	 * @param delta Time in seconds since the last frame.
	 */
	private void updateFadingOut(float delta) {
		m_fadeFactor -= delta / FADE_TIME;
		m_source.setVolume(m_finalVolume * m_fadeFactor);

		if (m_fadeFactor <= 0) {
			m_source.stop();
		}
	}

	/**
	 * Stops the source playing the sound.
	 */
	protected void stop() {
		if (m_active) {
			m_source.stop();
		}
	}

	/**
	 * @return {@code true} if the source that the controller is assigned to is still playing the sound.
	 */
	protected boolean isActive() {
		return m_active;
	}

	/**
	 * Indicates that the source is no longer playing the sound that this controller was created for, rendering the controller inactive.
	 */
	protected void setInactive() {
		m_active = false;
	}

	/**
	 * Indicates that the sound needs to be faded out.
	 */
	protected void fadeOut() {
		m_fading = true;
		m_finalVolume = m_source.getVolume();
	}

	/**
	 * Sets the position of the source (as long as the source is still playing the sound that this controller was created for).
	 *
	 * @param position The new position of the source.
	 */
	protected void setPosition(Vector3f position) {
		if (m_active) {
			m_source.setPosition(position);
		}
	}

	/**
	 * @return The volume that the sound is currently being played at.
	 */
	protected float getVolume() {
		return m_source.getVolume();
	}
}
