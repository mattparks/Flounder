package flounder.events;

public interface IEvent {
	boolean eventTriggered();

	void onEvent();
}
