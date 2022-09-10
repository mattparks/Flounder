package com.flounder.maths;

import com.flounder.framework.*;

/**
 * A class for handing and calculation deltas.
 */
public class Delta {
	private double currentFrameTime;
	private double lastFrameTime;

	private double delta;
	private double time;

	/**
	 * Creates a new delta handler.
	 */
	public Delta() {
		currentFrameTime = 0.0;
		lastFrameTime = 0.0;

		delta = 0.0;
		time = 0.0;
	}

	/**
	 * Updates delta and times.
	 */
	public void update() {
		currentFrameTime = Framework.get().getTimeMs() / 1000.0;
		delta = currentFrameTime - lastFrameTime;
		lastFrameTime = currentFrameTime;
		time += delta;
	}

	/**
	 * Gets the current delta.
	 *
	 * @return The delta.
	 */
	public double getDelta() {
		return delta;
	}

	/**
	 * Gets the time, all deltas added up.
	 *
	 * @return The time.
	 */
	public double getTime() {
		return time;
	}
}
