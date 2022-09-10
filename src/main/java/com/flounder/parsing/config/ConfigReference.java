package com.flounder.parsing.config;

/**
 * A reference to the value that was loaded from the config.
 */
@FunctionalInterface
public interface ConfigReference<T> {
	/**
	 * Gets the reading from that value.
	 *
	 * @return The value read.
	 */
	T getReading();
}
