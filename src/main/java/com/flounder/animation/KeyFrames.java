package com.flounder.animation;

import java.util.*;

/**
 * Represents one keyframe of an animation. This contains the timestamp of the keyframe,
 * which is the time (in seconds) from the start of the animation when this keyframe occurs.
 * <p>
 * It also contains the desired local-space transforms of all of the joints in the animated entity at this keyframe in the animation.
 * The joint transforms are stored in a map, indexed by the name of the joint that they should be applied to.
 */
public class KeyFrames {
	private final float timeStamp;
	private final Map<String, JointTransform> pose;

	/**
	 * Creates a new keyframe at a timestamp.
	 *
	 * @param timeStamp The time (in seconds) that this keyframe occurs during the animation.
	 * @param pose The local-space transforms for all the joints at this keyframe, indexed by the name of the joint that they should be applied to.
	 */
	public KeyFrames(float timeStamp, Map<String, JointTransform> pose) {
		this.timeStamp = timeStamp;
		this.pose = pose;
	}

	/**
	 * Gets the time in seconds of the keyframe in the animation.
	 *
	 * @return The time in seconds.
	 */
	public float getTimeStamp() {
		return timeStamp;
	}

	/**
	 * Gets the desired local-space transforms of all the joints at this keyframe, of the animation,
	 * indexed by the name of the joint that they correspond to.
	 *
	 * @return The desired local-space transforms.
	 */
	public Map<String, JointTransform> getPose() {
		return pose;
	}
}
