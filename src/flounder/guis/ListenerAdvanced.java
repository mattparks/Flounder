package flounder.guis;

/**
 * A advances GUI listener.
 */
public interface ListenerAdvanced {
	/**
	 * Gets if the event has occurred.
	 *
	 * @return The event has occurred.
	 */
	boolean hasOccurred();

	/**
	 * Run when a event has occurred.
	 */
	void run();
}
