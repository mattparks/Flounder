package flounder.maths;

import flounder.engine.*;

/**
 * A class for handing and calculation deltas.
 */
public class Delta {
	private float currentFrameTime;
	private float lastFrameTime;

	private float delta;
	private float time;

	/**
	 * Creates a new delta handler.
	 */
	public Delta() {
		currentFrameTime = 0.0f;
		lastFrameTime = 0.0f;

		delta = 0.0f;
		time = 0.0f;
	}

	/**
	 * Updates delta and times.
	 */
	public void update() {
		currentFrameTime = FlounderEngine.getDevices().getTime() / 1000.0f;
		delta = currentFrameTime - lastFrameTime;
		lastFrameTime = currentFrameTime;
		time += delta;
	}

	/**
	 * Gets the current delta.
	 *
	 * @return The delta.
	 */
	public float getDelta() {
		return delta;
	}

	/**
	 * Gets the time, all deltas added up.
	 *
	 * @return The time.
	 */
	public float getTime() {
		return time;
	}
}
