package com.flounder.events;

/**
 * A class that acts as a basic change listener for a value.
 *
 * @param <T> The type of value to find change with.
 */
public abstract class EventChange<T> implements IEvent {
	private ValueReference<T> reference;
	private T current;

	/**
	 * Creates a new change event.
	 *
	 * @param reference The reference to listen to.
	 */
	public EventChange(ValueReference<T> reference) {
		this.reference = reference;
	}

	@Override
	public boolean eventTriggered() {
		T newValue = reference.get();

		if (newValue == null) {
			return false;
		}

		boolean triggered = !newValue.equals(current);
		current = newValue;
		return triggered;
	}

	@Override
	public void onEvent() {
		onEvent(current);
	}

	@Override
	public boolean removeAfterEvent() {
		return false;
	}

	/**
	 * A implementation of IEvents onEvent but passes the new value.
	 *
	 * @param newValue The new value.
	 */
	public abstract void onEvent(T newValue);

	/**
	 * A reference to a value.
	 */
	@FunctionalInterface
	public interface ValueReference<T> {
		/**
		 * Gets the value.
		 *
		 * @return The value.
		 */
		T get();
	}
}
