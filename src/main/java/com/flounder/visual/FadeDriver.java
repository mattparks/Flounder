package com.flounder.visual;

/**
 * A driver that fades from start to end.
 */
public class FadeDriver extends ValueDriver {
	private float start;
	private float end;
	private float peak;

	/**
	 * Creates a new fade driver.
	 *
	 * @param peak The peak value.
	 * @param start The start time.
	 * @param end The end time.
	 * @param duration The time taken to get to the end.
	 */
	public FadeDriver(float peak, float start, float end, float duration) {
		super(duration);
		this.peak = peak;
		this.start = start;
		this.end = end;
	}

	/**
	 * Gets the start time.
	 *
	 * @return The start time.
	 */
	public float getStart() {
		return start;
	}

	/**
	 * Sets the start time.
	 *
	 * @param start The new start time.
	 */
	public void setStart(float start) {
		this.start = start;
	}

	/**
	 * Gets the end time.
	 *
	 * @return The ebd time.
	 */
	public float getEnd() {
		return end;
	}

	/**
	 * Sets the end time.
	 *
	 * @param end The new end time.
	 */
	public void setEnd(float end) {
		this.end = end;
	}

	/**
	 * Gets the peak value.
	 *
	 * @return The peak value.
	 */
	public float getPeak() {
		return peak;
	}

	/**
	 * Sets the peak value.
	 *
	 * @param peak The new peak value.
	 */
	public void setPeak(float peak) {
		this.peak = peak;
	}

	@Override
	protected float calculateValue(float time) {
		if (time < start) {
			return time / start * peak;
		} else if (time > end) {
			return (1 - (time - end) / (1 - end)) * peak;
		} else {
			return peak;
		}
	}
}
