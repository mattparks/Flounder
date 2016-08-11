package flounder.events;

import flounder.engine.*;

import java.util.*;

public class FlounderEvents implements IModule {
	private ArrayList<IEvent> events;

	@Override
	public void init() {
		events = new ArrayList<>();
	}

	@Override
	public void update() {
		for (IEvent event : events) {
			if (event.eventTriggered()) {
				event.onEvent();
			}
		}
	}

	public void addEvent(IEvent event) {
		events.add(event);
	}

	public void removeEvent(IEvent event) {
		events.remove(event);
	}

	@Override
	public void profile() {
	}

	@Override
	public void dispose() {
	}
}
