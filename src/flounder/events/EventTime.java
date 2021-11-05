package flounder.events;

import flounder.maths.*;

/**
 * A class that runs a event after a time has passed.
 */
public abstract class EventTime implements IEvent {
	private Timer timer;
	private boolean repeat;

	/**
	 * Creates a new time event.
	 *
	 * @param interval The amount of seconds in the future to run the event.
	 * @param repeat If the event will repeat after the first run.
	 */
	public EventTime(float interval, boolean repeat) {
		this.timer = new Timer(interval);
		this.repeat = repeat;
	}

	@Override
	public boolean eventTriggered() {
		if (timer.isPassedTime()) {
			timer.resetStartTime();
			return true;
		}

		return false;
	}

	@Override
	public boolean removeAfterEvent() {
		return !repeat;
	}
}
