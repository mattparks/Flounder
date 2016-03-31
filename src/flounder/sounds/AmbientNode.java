package flounder.sounds;

import flounder.devices.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.visual.*;

import java.util.*;

/**
 * Basically a 3D sphere of sound that can be added to the world to provide ambient sound effects to a certain area. The node has 2 radius's; within the
 * inner radius the ambient sound is played at full volume. Between the inner and outer radius's the volume decreases the further away from the node you get.
 */
public class AmbientNode {
	private static final float RADIUS_CHANGE_AGIL = 0.5f;
	private static final float RANGE_THRESHOLD = 1.2f;

	private Vector3f m_position;
	private SmoothFloat m_innerRadius;
	private SmoothFloat m_fadeOutRadius;

	private List<Sound> m_sounds;
	private float m_volume;

	private boolean m_active;
	private AudioController m_controller;

	private Sound m_lastPlayed;

	/**
	 * Creates a new ambient node at a given position in the world.
	 *
	 * @param position The position of the center of the node.
	 * @param innerRange The inner radius, within which the ambient sound is played at full volume.
	 * @param fadeOutRange The distance between the inner and outer radius's of the node.
	 * @param sounds The various sounds that the node can play.
	 */
	public AmbientNode(final Vector3f position, final float innerRange, final float fadeOutRange, final List<Sound> sounds) {
		m_position = position;
		m_innerRadius = new SmoothFloat(innerRange, RADIUS_CHANGE_AGIL);
		m_fadeOutRadius = new SmoothFloat(fadeOutRange, RADIUS_CHANGE_AGIL);
		m_sounds = sounds;
		m_volume = 1.0f;
		m_active = false;
		m_controller = null;
		m_lastPlayed = null;
	}

	/**
	 * Updates the node and plays or stops playing ambient sounds depending on whether the {@link IAudioListener} is in range or not.
	 *
	 * @param delta The time in seconds since the last frame.
	 */
	public void update(final float delta) {
		updateValues(delta);
		float distance = getDistanceFromListener();

		if (!m_active && distance <= getRange()) {
			playNewSound();
		} else if (m_active) {
			updateActiveNode(delta, distance);
		}
	}

	/**
	 * Updates the smooth floats to slowly change the radius values.
	 *
	 * @param delta The time in seconds since the last frame.
	 */
	private void updateValues(final float delta) {
		m_innerRadius.update(delta);
		m_fadeOutRadius.update(delta);

		if (m_controller != null) {
			// FIXME: Update ranges.
		}
	}

	/**
	 * @return the distance between the {@link IAudioListener} and the node's center.
	 */
	private float getDistanceFromListener() {
		return Vector3f.subtract(ManagerDevices.getSound().getCameraPosition(), m_position, null).length();
	}

	/**
	 * Starts playing a random sound from the available sounds list.
	 */
	private void playNewSound() {
		Sound sound = chooseNextSound();
		PlayRequest request = PlayRequest.new3dSoundPlayRequest(sound, m_volume, m_position, m_innerRadius.get(), getRange());
		m_controller = ManagerDevices.getSound().play3DSound(request);
		m_active = m_controller != null;
	}

	/**
	 * Chooses a random sound from the available sounds list, and if there's more than one available sound it ensures that the previously played sound isn't repeated.
	 *
	 * @return The next sound.
	 */
	private Sound chooseNextSound() {
		Sound sound = null;
		int index = Maths.RANDOM.nextInt(m_sounds.size());

		if (m_sounds.size() > 1 && sound == m_lastPlayed) {
			index = (index + 1) % m_sounds.size();
		}

		sound = m_sounds.get(index);
		m_lastPlayed = sound;
		return sound;
	}

	/**
	 * Checks whether the current ambient sound should stop being played because the listener has gone far enough away from the outer radius, otherwise
	 * the currently playing sound's {@link AudioController} is updated, and a new sound is played if the current sound has come to an end.
	 *
	 * @param delta The time in seconds since the last frame.
	 * @param distance The distance of the {@link IAudioListener} from the node's center.
	 */
	private void updateActiveNode(final float delta, final float distance) {
		if (distance >= getRange() * RANGE_THRESHOLD) {
			m_controller.stop();
			m_active = false;
		} else {
			boolean stillPlaying = m_controller.update(delta);

			if (!stillPlaying) {
				playNewSound();
			}
		}
	}

	/**
	 * @return The distance from the center of the node to the outer radius.
	 */
	public float getRange() {
		return m_innerRadius.get() + m_fadeOutRadius.get();
	}

	/**
	 * Set the inner and outer radiuses of the node.
	 *
	 * @param innerRange The distance from the center of the node to the inner radius. Within this radius the ambient sounds are played at full volume.
	 * @param fadeOutRange The distance between the inner and outer radius's.
	 */
	public void setRanges(final float innerRange, final float fadeOutRange) {
		m_innerRadius.set(innerRange);
		m_fadeOutRadius.set(fadeOutRange);
	}

	/**
	 * Sets the available sounds that this ambient node can play. Fades out any current sounds that are playing before playing any of the new sounds.
	 *
	 * @param sounds The list of sounds.
	 */
	public void setSounds(final List<Sound> sounds) {
		m_sounds = sounds;

		if (m_controller != null) {
			m_controller.fadeOut();
		}
	}

	/**
	 * Sets a single sound for the ambient not to play on repeat. Fades out any current sounds that are playing before playing the new sound.
	 *
	 * @param sound The sound to be played on repeat.
	 */
	public void setSound(final Sound sound) {
		m_sounds.clear();
		m_sounds.add(sound);

		if (m_controller != null) {
			m_controller.fadeOut();
		}
	}

	/**
	 * Adds a sound to the list of available ambient sounds for this node to play.
	 *
	 * @param sound The new sound to be added to the list.
	 */
	public void addSound(final Sound sound) {
		m_sounds.add(sound);
	}

	/**
	 * Removes a sound from the available list of sounds for this node.
	 *
	 * @param sound The sound to remove.
	 */
	public void removeSound(final Sound sound) {
		m_sounds.remove(sound);
	}

	/**
	 * Sets the volume of this node.
	 *
	 * @param targetVolume The desired volume.
	 */
	public void setVolume(final float targetVolume) {
		m_volume = targetVolume;
	}
}
