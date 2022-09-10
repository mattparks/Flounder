package com.flounder.visual;

/**
 * A driver that interpolates between keyframes.
 */
public class KeyFrameDriver extends ValueDriver {
	private KeyFrame[] keyFrames;

	/**
	 * Creates a new keyframe driver.
	 *
	 * @param keyFrames The keyframes to go though.
	 * @param length The drivers length.
	 */
	public KeyFrameDriver(KeyFrame[] keyFrames, float length) {
		super(length);
		this.keyFrames = keyFrames;
	}

	@Override
	protected float calculateValue(float time) {
		int index = findNextFrameIndex(time, 0, keyFrames.length - 1);
		KeyFrame previous = keyFrames[index - 1];
		KeyFrame next = keyFrames[index];
		float factor = (time - previous.getTime()) / (next.getTime() - previous.getTime());
		float difference = next.getValue() - previous.getValue();
		return previous.getValue() + factor * difference;
	}

	/**
	 * Finds the next frame.
	 *
	 * @param time The current time.
	 * @param firstIndex The first index.
	 * @param lastIndex The last index.
	 *
	 * @return The next index.
	 */
	private int findNextFrameIndex(float time, int firstIndex, int lastIndex) {
		if (firstIndex == lastIndex) {
			return lastIndex + 1;
		}

		float length = 1 + lastIndex - firstIndex;
		int check = (int) Math.floor(length / 2.0f) + firstIndex - 1;
		float number1 = keyFrames[check].getTime();
		float number2 = keyFrames[check + 1].getTime();

		if (number1 > time) {
			return findNextFrameIndex(time, firstIndex, check);
		} else {
			if (number2 > time) {
				return check + 1;
			} else {
				return findNextFrameIndex(time, check + 1, lastIndex);
			}
		}
	}
}
