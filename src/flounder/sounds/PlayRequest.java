package flounder.sounds;

import flounder.devices.*;
import flounder.engine.options.*;
import flounder.maths.vectors.*;

/**
 * A request that can be sent to the {@link DeviceSound} specifying what sound should be played and what settings the source playing it should use.
 */
public class PlayRequest {
	private Vector3f m_position;
	private float m_innerRange;
	private float m_outerRange;

	private boolean m_systemSound;
	private boolean m_loop;
	private float m_volume;

	private Sound m_sound;

	/**
	 * Creates a new sound play request.
	 *
	 * @param sound The sound that needs to be played.
	 * @param volume The volume that the sound should be played at.
	 */
	private PlayRequest(final Sound sound, final float volume) {
		m_position = new Vector3f(0.0f, 0.0f, 0.0f);
		m_innerRange = 1.0f;
		m_outerRange = 1.0f;
		m_systemSound = true;
		m_loop = false;
		m_volume = volume * OptionsAudio.SOUND_VOLUME;
		m_sound = sound;
	}

	/**
	 * Creates a new request for playing a "system sound", which is a sound that isn't part of the 3D world and is simple played at full volume regardless of where the {@link IAudioListener} is.
	 *
	 * @param systemSound The sound to be played.
	 *
	 * @return The newly created play request.
	 */
	public static PlayRequest newSystemPlayRequest(final Sound systemSound) {
		return new PlayRequest(systemSound, 1);
	}

	/**
	 * Creates a new request for playing a 3D sound, which is a sound that should appear to be emitted from somewhere in the 3D world.
	 *
	 * @param sound The sound to be played.
	 * @param volume The volume of the sound.
	 * @param position The position from where the sound should be emitted.
	 * @param innerRange The inner range of the sound. Within this range of the sound's position the sound is played at full volume.
	 * @param outerRange The outer range of the sound. Outside this range from the sound's position the sound can't be heard at all.
	 *
	 * @return The newly created request.
	 */
	public static PlayRequest new3dSoundPlayRequest(final Sound sound, final float volume, final Vector3f position, final float innerRange, final float outerRange) {
		PlayRequest request = new PlayRequest(sound, volume);
		request.m_systemSound = false;
		request.m_innerRange = innerRange < 1 ? 1 : innerRange;
		request.m_outerRange = outerRange;
		request.m_position = position;
		return request;
	}

	/**
	 * @return The position in the 3D world where the sound should be emitted from.
	 */
	public final Vector3f getPosition() {
		return m_position;
	}

	/**
	 * @return The inner range of the sound. Inside this range the sound is heard at full volume.
	 */
	public final float getInnerRange() {
		return m_innerRange;
	}

	/**
	 * @return The total range of the sound. Outside this range the sound cannot be heard.
	 */
	public final float getOuterRange() {
		return m_outerRange;
	}

	/**
	 * @return {@code true} if the sound is a "system sound", meaning that it should be played at full volume regardless of where the {@link IAudioListener} is.
	 */
	public final boolean isSystemSound() {
		return m_systemSound;
	}

	/**
	 * @return {@code true} if the sound should be played on loop.
	 */
	public final boolean isLooping() {
		return m_loop;
	}

	/**
	 * @return The volume at which this sound should be played.
	 */
	public final float getVolume() {
		return m_volume;
	}

	/**
	 * @return The sound that is being requested to be played.
	 */
	public final Sound getSound() {
		return m_sound;
	}

	/**
	 * Indicates that the request is requesting the sound to be played on loop.
	 *
	 * @param loop Whether the sound should be played on loop or not.
	 */
	public void setLooping(final boolean loop) {
		m_loop = loop;
	}
}
