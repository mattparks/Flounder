package flounder.events;

import flounder.engine.*;

import java.util.*;

public class FlounderEvents extends IModule {
	private static final FlounderEvents instance = new FlounderEvents();

	private ArrayList<IEvent> events;

	/**
	 * Creates a new event manager.
	 */
	public FlounderEvents() {
		super(ModuleUpdate.BEFORE_ENTRANCE);
	}

	@Override
	public void init() {
		this.events = new ArrayList<>();
	}

	@Override
	public void run() {
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
	public IModule getInstance() {
		return instance;
	}

	@Override
	public void dispose() {
		events.clear();
	}
}
