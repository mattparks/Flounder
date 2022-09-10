package com.flounder.helpers;

/**
 * A helper for creating a simple single type.
 *
 * @param <FIRST> The object in the single.
 */
public class Single<FIRST> {
	private FIRST single;

	/**
	 * Creates a null pair.
	 */
	public Single() {
		this(null);
	}

	/**
	 * Creates a filled single.
	 *
	 * @param single The single value.
	 */
	public Single(FIRST single) {
		this.single = single;
	}

	/**
	 * Gets the single value.
	 *
	 * @return The single value.
	 */
	public FIRST getSingle() {
		return single;
	}

	/**
	 * Sets the single value.
	 *
	 * @param single The single value.
	 */
	public void setSingle(FIRST single) {
		this.single = single;
	}

	@Override
	public String toString() {
		return "Single{" +
				"single=" + single +
				'}';
	}
}
