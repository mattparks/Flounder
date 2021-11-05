package flounder.processing;

import flounder.framework.*;

/**
 * A extension used with {@link flounder.processing.FlounderProcessors} to define a processor.
 */
public abstract class Processor extends Extension {
	/**
	 * Creates a new processor.
	 *
	 * @param requires The classes that are extra requirements for this implementation.
	 */
	public Processor(Class... requires) {
		super(FlounderProcessors.class, requires);
	}

	/**
	 * Run when initializing the processor.
	 */
	public abstract void init();

	/**
	 * Run when updating the processor.
	 */
	public abstract void update();

	/**
	 * Used to add a request into the processor.
	 *
	 * @param request The request object to add to the que.
	 */
	public abstract void addRequestToQueue(Object request);

	/**
	 * Gets the class used for requests.
	 *
	 * @return The request class used.
	 */
	public abstract Class getRequestClass();

	/**
	 * Run when disposing the processor.
	 */
	public abstract void dispose();

	@Override
	public abstract boolean isActive();
}
