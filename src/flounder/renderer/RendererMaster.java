package flounder.renderer;

import flounder.framework.*;

/**
 * A extension used with {@link flounder.renderer.FlounderRenderer} to define a master renderer.
 */
public abstract class RendererMaster extends Extension {
	/**
	 * Creates a new master renderer.
	 *
	 * @param requires The classes that are extra requirements for this implementation.
	 */
	public RendererMaster(Class... requires) {
		super(FlounderRenderer.class, requires);
	}

	/**
	 * Run when initializing the master renderer.
	 */
	public abstract void init();

	/**
	 * Run when rendering the master renderer.
	 */
	public abstract void render();

	/**
	 * Run when profiling the master renderer.
	 */
	public abstract void profile();

	/**
	 * Run when disposing the master renderer.
	 */
	public abstract void dispose();

	@Override
	public abstract boolean isActive();
}
