package flounder.standards;

import flounder.framework.*;

/**
 * A extension used with {@link flounder.standards.FlounderStandard} to define a standards.
 */
public abstract class Standard extends Extension {
	/**
	 * Creates a new standards.
	 *
	 * @param requires The classes that are extra requirements for this implementation.
	 */
	public Standard(Class... requires) {
		super(FlounderStandard.class, requires);
	}

	/**
	 * Run when initializing the standards.
	 */
	public abstract void init();

	/**
	 * Run when updating the standards.
	 */
	public abstract void update();

	/**
	 * Run when profiling the standards.
	 */
	public abstract void profile();

	/**
	 * Run when disposing the standards.
	 */
	public abstract void dispose();

	@Override
	public abstract boolean isActive();
}
