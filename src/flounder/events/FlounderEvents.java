package flounder.events;

import flounder.engine.*;

import java.util.*;

public class FlounderEvents extends IModule {
	private static final FlounderEvents instance = new FlounderEvents();

	private ArrayList<IEvent> events;

	public FlounderEvents() {
		super();
	}

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

	public static void addEvent(IEvent event) {
		instance.events.add(event);
	}

	public static void removeEvent(IEvent event) {
		instance.events.remove(event);
	}

	@Override
	public void profile() {
	}

	@Override
	public void dispose() {
	}
}
