package flounder.events;

/**
 * A class that is the most basic implementation of the IEvent interface.
 */
public abstract class EventStandard implements IEvent {
	private boolean repeat;

	/**
	 * Creates a new standard event.
	 *
	 * @param repeat If the event will repeat after the first run.
	 */
	public EventStandard(boolean repeat) {
		this.repeat = repeat;
	}

	/**
	 * Creates a new standard event that repeats.
	 */
	public EventStandard() {
		this.repeat = true;
	}

	@Override
	public boolean removeAfterEvent() {
		return !repeat;
	}
}
