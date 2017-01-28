package flounder.visual;

/**
 * A keyframe that has a value at a time.
 */
public class KeyFrame {
	private float value;
	private float time;

	/**
	 * Creates a new keyframe.
	 *
	 * @param time The time.
	 * @param value The value.
	 */
	public KeyFrame(float time, float value) {
		this.time = time;
		this.value = value;
	}

	/**
	 * Gets the value.
	 *
	 * @return The value.
	 */
	protected float getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value The new value.
	 */
	protected void setValue(float value) {
		this.value = value;
	}

	/**
	 * Gets the time.
	 *
	 * @return The time.
	 */
	protected float getTime() {
		return time;
	}

	/**
	 * Sets the time.
	 *
	 * @param time The new time.
	 */
	protected void setTime(float time) {
		this.time = time;
	}
}
