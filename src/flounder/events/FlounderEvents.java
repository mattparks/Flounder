package flounder.events;

import flounder.framework.*;

import java.util.*;

/**
 * A module used for managing events on framework updates.
 */
public class FlounderEvents extends flounder.framework.Module {
	private List<IEvent> events;
	private List<IEvent> clones;

	/**
	 * Creates a new event manager.
	 */
	public FlounderEvents() {
		super();
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
		this.events = new ArrayList<>();
		this.clones = new ArrayList<>();
	}

	@Handler.Function(Handler.FLAG_UPDATE_PRE)
	public void update() {
		clones.clear();
		clones.addAll(events);

		clones.forEach(event -> {
			if (event.eventTriggered()) {
				event.onEvent();

				if (event.removeAfterEvent()) {
					events.remove(event);
				}
			}
		});
	}

	/**
	 * Adds an event to the listening que.
	 *
	 * @param event The event to add.
	 */
	public void addEvent(IEvent event) {
		this.events.add(event);
	}

	/**
	 * Removes a event to the listening que.
	 *
	 * @param event The event to remove.
	 */
	public void removeEvent(IEvent event) {
		this.events.remove(event);
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
		events.clear();
	}

	@flounder.framework.Module.Instance
	public static FlounderEvents get() {
		return (FlounderEvents) Framework.get().getModule(FlounderEvents.class);
	}
}
