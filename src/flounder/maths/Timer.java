package flounder.maths;

/**
 * A timer implementation for events.
 */
public class Timer {
	private long startTime;
	private float interval;

	/**
	 * Creates a new timer.
	 *
	 * @param interval The time between events.
	 */
	public Timer(float interval) {
		this.startTime = System.currentTimeMillis();
		this.interval = interval;
	}

	/**
	 * Gets if the interval has been passes for the timer.
	 *
	 * @return If the interval was exceeded.
	 */
	public boolean pastTargetTime() {
		return System.currentTimeMillis() - startTime > interval;
	}

	/**
	 * Adds the intervals value to the start time.
	 */
	public void addToStartTime() {
		startTime += (long) interval;
	}

	/**
	 * Gets what the interval is.
	 *
	 * @return The timers current interval.
	 */
	public float getInterval() {
		return interval;
	}

	/**
	 * Gets the timers interval. (Resets timer).
	 *
	 * @param interval The new timer interval.
	 */
	public void setInterval(float interval) {
		this.interval = interval;
		this.startTime = System.currentTimeMillis();
	}
}
