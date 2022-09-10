package com.flounder.maths;

import com.flounder.framework.*;

/**
 * A timer implementation for events.
 */
public class Timer {
	private double startTime;
	private double interval;

	/**
	 * Creates a new timer.
	 *
	 * @param interval The time between events (seconds).
	 */
	public Timer(double interval) {
		if (Framework.get() != null) {
			this.startTime = Framework.get().getTimeMs();
		} else {
			this.startTime = 0.0f;
		}

		this.interval = interval * 1000.0;
	}

	/**
	 * Gets if the interval has been passes for the timer.
	 *
	 * @return If the interval was exceeded.
	 */
	public boolean isPassedTime() {
		return Framework.get().getTimeMs() - startTime > interval;
	}

	/**
	 * Adds the intervals value to the start time.
	 */
	public void resetStartTime() {
		startTime = Framework.get().getTimeMs();
	}

	/**
	 * Gets what the interval is. (Seconds).
	 *
	 * @return The timers current interval.
	 */
	public double getInterval() {
		return interval / 1000.0;
	}

	/**
	 * Gets the timers interval. (Seconds, Resets timer).
	 *
	 * @param interval The new timer interval.
	 */
	public void setInterval(double interval) {
		this.interval = (long) (interval * 1000.0);
		this.startTime = Framework.get().getTimeMs();
	}
}
