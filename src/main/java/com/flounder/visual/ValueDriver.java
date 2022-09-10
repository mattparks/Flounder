package com.flounder.visual;

/**
 * Represents a driver that changes over time.
 */
public abstract class ValueDriver {
	private float length;
	private float currentTime;

	/**
	 * Creates a new driver with a length.
	 *
	 * @param length The drivers length.
	 */
	public ValueDriver(float length) {
		currentTime = 0.0f;
		this.length = length;
	}

	/**
	 * Updates the driver with the passed time.
	 *
	 * @param delta The time between the last update.
	 *
	 * @return The calculated value.
	 */
	public float update(float delta) {
		currentTime += delta;
		currentTime %= length;
		float time = currentTime / length;
		return calculateValue(time);
	}

	/**
	 * Calculates the new value.
	 *
	 * @param time The time into the drivers life.
	 *
	 * @return The calculated value.
	 */
	protected abstract float calculateValue(float time);
}
