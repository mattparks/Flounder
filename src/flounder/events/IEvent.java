package flounder.events;

/**
 * A simple event listener and runner.
 */
public interface IEvent {
	/**
	 * Gets if the event has occurred.
	 *
	 * @return The event has occurred.
	 */
	boolean eventTriggered();

	/**
	 * Run when a event has occurred.
	 */
	void onEvent();
}
