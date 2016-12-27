package flounder.guis;

import flounder.framework.*;

/**
 * A interface used to manage a main GUI system.
 */
public abstract class IGuiMaster extends IExtension {
	/**
	 * Creates a new GUI master.
	 *
	 * @param requires The classes that are extra requirements for this implementation.
	 */
	public IGuiMaster(Class... requires) {
		super(FlounderGuis.class, requires);
	}

	/**
	 * Run when initializing the GUI master.
	 */
	public abstract void init();

	/**
	 * Run when updating the GUI master.
	 */
	public abstract void update();

	/**
	 * Run when profiling the GUI master.
	 */
	public abstract void profile();

	/**
	 * Gets if the main menu is open.
	 *
	 * @return If the main menu is open.
	 */
	public abstract boolean isGamePaused();

	/**
	 * Forces the main GUI to open.
	 */
	public abstract void openMenu();

	/**
	 * Gets the main menu's blur factor.
	 *
	 * @return The main menu's blur factor.
	 */
	public abstract float getBlurFactor();

	/**
	 * Run when disposing the GUI master.
	 */
	public abstract void dispose();

	@Override
	public abstract boolean isActive();
}
