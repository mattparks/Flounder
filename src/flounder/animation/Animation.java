package flounder.animation;

/**
 * Represents an animation that can be carried out by an animated entity.
 * It contains the length of the animation in seconds, and a list of {@link KeyFrames}s.
 */
public class Animation {
	private final float length;
	private final KeyFrames[] keyFrames;

	/**
	 * Creates a new animation.
	 *
	 * @param lengthInSeconds The length of the animation in seconds.
	 * @param keyFrames All the keyframes for the animation, ordered by time of appearance in the animation.
	 */
	public Animation(float lengthInSeconds, KeyFrames[] keyFrames) {
		this.keyFrames = keyFrames;
		this.length = lengthInSeconds;
	}

	/**
	 * Gets the length of the animation in seconds.
	 *
	 * @return The length of the animation.
	 */
	public float getLength() {
		return length;
	}

	/**
	 * Gets an array of the animation's keyframes. The array is ordered based on the order of the
	 * keyframes in the animation (first keyframe of the animation in array position 0).
	 *
	 * @return The array of the animation's keyframes.
	 */
	public KeyFrames[] getKeyFrames() {
		return keyFrames;
	}
}
