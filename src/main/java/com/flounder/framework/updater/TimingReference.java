package com.flounder.framework.updater;

/**
 * A reference to a time fetching function
 */
public interface TimingReference<T> {
	/**
	 * Gets the time from the function.
	 *
	 * @return The time read in seconds.
	 */
	double getTime();
}
