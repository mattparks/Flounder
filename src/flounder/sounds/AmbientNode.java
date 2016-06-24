package flounder.sounds;

import flounder.engine.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.visual.interpolation.*;

import java.util.*;

/**
 * Basically a 3D sphere of sound that can be added to the world to provide ambient sound effects to a certain area. The node has 2 radius's; within the
 * inner radius the ambient sound is played at full volume. Between the inner and outer radius's the volume decreases the further away from the node you get.
 */
public class AmbientNode {
	private static final float RADIUS_CHANGE_AGIL = 0.5f;
	private static final float RANGE_THRESHOLD = 1.2f;

	private Vector3f position;
	private List<Sound> sounds;
	private SmoothFloat innerRadius;
	private SmoothFloat fadeOutRadius;
	private float volume;

	private boolean active;
	private AudioController controller;

	private Sound lastPlayed;

	/**
	 * Creates a new ambient node at a given position in the world.
	 *
	 * @param position The position of the centre of the node.
	 * @param innerRange The inner radius, within which the ambient sound is played at full volume.
	 * @param fadeOutRange The distance between the inner and outer radius's of the node.
	 * @param sounds The various sounds that the node can play.
	 */
	public AmbientNode(Vector3f position, float innerRange, float fadeOutRange, List<Sound> sounds) {
		this.position = position;
		this.innerRadius = new SmoothFloat(innerRange, RADIUS_CHANGE_AGIL);
		this.fadeOutRadius = new SmoothFloat(fadeOutRange, RADIUS_CHANGE_AGIL);

		this.sounds = sounds;
		this.volume = 1.0f;

		this.active = false;
	}

	/**
	 * Updates the node and plays or stops playing ambient sounds depending on whether the {@link IAudioListener} is in range or not.
	 *
	 * @param delta The time in seconds since the last frame.
	 */
	public void update(float delta) {
		updateValues(delta);
		float distance = getDistanceFromListener();

		if (!active && distance <= getRange()) {
			playNewSound();
		} else if (active) {
			updateActiveNode(delta, distance);
		}
	}

	/**
	 * Updates the smooth floats to slowly change the radius values.
	 *
	 * @param delta The time in seconds since the last frame.
	 */
	private void updateValues(float delta) {
		innerRadius.update(delta);
		fadeOutRadius.update(delta);
	}

	/**
	 * @return the distance between the {@link IAudioListener} and the node's centre.
	 */
	private float getDistanceFromListener() {
		return Vector3f.subtract(FlounderEngine.getDevices().getSound().getCameraPosition(), position, null).length();
	}

	/**
	 * Starts playing a random sound from the available sounds list.
	 */
	private void playNewSound() {
		Sound sound = chooseNextSound();
		PlayRequest request = PlayRequest.new3dSoundPlayRequest(sound, volume, position, innerRadius.get(), getRange());
		controller = FlounderEngine.getDevices().getSound().play3DSound(request);
		active = controller != null;
	}

	/**
	 * Chooses a random sound from the available sounds list, and if there's more than one available sound it ensures that the previously played sound isn't repeated.
	 *
	 * @return The next sound.
	 */
	private Sound chooseNextSound() {
		Sound sound = null;
		int index = Maths.RANDOM.nextInt(sounds.size());

		if (sounds.size() > 1 && sound == lastPlayed) {
			index = (index + 1) % sounds.size();
		}

		sound = sounds.get(index);
		lastPlayed = sound;
		return sound;
	}

	/**
	 * Checks whether the current ambient sound should stop being played because the listener has gone far enough away from the outer radius, otherwise
	 * the currently playing sound's {@link AudioController} is updated, and a new sound is played if the current sound has come to an end.
	 *
	 * @param delta The time in seconds since the last frame.
	 * @param distance The distance of the {@link IAudioListener} from the node's centre.
	 */
	private void updateActiveNode(float delta, float distance) {
		if (distance >= getRange() * RANGE_THRESHOLD) {
			controller.stop();
			active = false;
		} else {
			if (!controller.update(delta)) {
				playNewSound();
			}
		}
	}

	/**
	 * @return The distance from the centre of the node to the outer radius.
	 */
	public float getRange() {
		return innerRadius.get() + fadeOutRadius.get();
	}

	/**
	 * Set the inner and outer radiuses of the node.
	 *
	 * @param innerRange The distance from the centre of the node to the inner radius. Within this radius the ambient sounds are played at full volume.
	 * @param fadeOutRange The distance between the inner and outer radius's.
	 */
	public void setRanges(float innerRange, float fadeOutRange) {
		innerRadius.set(innerRange);
		fadeOutRadius.set(fadeOutRange);
	}

	/**
	 * Sets the available sounds that this ambient node can play. Fades out any current sounds that are playing before playing any of the new sounds.
	 *
	 * @param sounds The list of sounds.
	 */
	public void setSounds(List<Sound> sounds) {
		this.sounds.clear();
		this.sounds.addAll(sounds);

		if (controller != null) {
			controller.fadeOut();
		}
	}

	/**
	 * Sets a single sound for the ambient not to play on repeat. Fades out any current sounds that are playing before playing the new sound.
	 *
	 * @param sound The sound to be played on repeat.
	 */
	public void setSound(Sound sound) {
		sounds.clear();
		sounds.add(sound);

		if (controller != null) {
			controller.fadeOut();
		}
	}

	/**
	 * Adds a sound to the list of available ambient sounds for this node to play.
	 *
	 * @param sound The new sound to be added to the list.
	 */
	public void addSound(Sound sound) {
		sounds.add(sound);
	}

	/**
	 * Removes a sound from the available list of sounds for this node.
	 *
	 * @param sound The sound to remove.
	 */
	public void removeSound(Sound sound) {
		sounds.remove(sound);
	}

	/**
	 * Sets the volume of this node.
	 *
	 * @param targetVolume The desired volume.
	 */
	public void setVolume(float targetVolume) {
		volume = targetVolume;
	}
}
