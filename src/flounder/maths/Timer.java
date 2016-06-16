package flounder.maths;

/**
 * A timer implementation for events.
 */
public class Timer {
	private long startTime;
	private long interval;

	/**
	 * Creates a new timer.
	 *
	 * @param interval The time between events (seconds).
	 */
	public Timer(float interval) {
		this.startTime = System.currentTimeMillis();
		this.interval = (long) (interval * 1000.0);
	}

	/**
	 * Gets if the interval has been passes for the timer.
	 *
	 * @return If the interval was exceeded.
	 */
	public boolean isPassedTime() {
		return System.currentTimeMillis() - startTime >= interval;
	}

	/**
	 * Adds the intervals value to the start time.
	 */
	public void resetStartTime() {
		startTime = System.currentTimeMillis();
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
	 * Gets the timers interval. (Seconds, Resets timer).
	 *
	 * @param interval The new timer interval.
	 */
	public void setInterval(float interval) {
		this.interval = (long) (interval * 1000.0);
		this.startTime = System.currentTimeMillis();
	}
}
