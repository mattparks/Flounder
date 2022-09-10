package com.flounder.helpers;

/**
 * A helper for creating a simple pair type.
 *
 * @param <FIRST> The first object in the pair.
 * @param <SECOND> The second object in the pair.
 */
public class Pair<FIRST, SECOND> {
	private FIRST first;
	private SECOND second;

	/**
	 * Creates a null pair.
	 */
	public Pair() {
		this(null, null);
	}

	/**
	 * Creates a filled pair.
	 *
	 * @param first The first value.
	 * @param second The second value.
	 */
	public Pair(FIRST first, SECOND second) {
		this.first = first;
		this.second = second;
	}

	/**
	 * Gets the first value.
	 *
	 * @return The first value.
	 */
	public FIRST getFirst() {
		return first;
	}

	/**
	 * Sets the first value.
	 *
	 * @param first The first value.
	 */
	public void setFirst(FIRST first) {
		this.first = first;
	}

	/**
	 * Gets the second value.
	 *
	 * @return The second value.
	 */
	public SECOND getSecond() {
		return second;
	}

	/**
	 * Sets the second value.
	 *
	 * @param second The second value.
	 */
	public void setSecond(SECOND second) {
		this.second = second;
	}

	@Override
	public String toString() {
		return "Pair{" +
				"first=" + first +
				", second=" + second +
				'}';
	}
}
