package com.flounder.guis;

import com.flounder.framework.*;
import com.flounder.maths.*;

/**
 * A interface used to manage a main GUI system.
 */
public abstract class GuiMaster extends Extension {
	/**
	 * Creates a new GUI master.
	 *
	 * @param requires The classes that are extra requirements for this implementation.
	 */
	public GuiMaster(Class... requires) {
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
	 * Gets the main menu's blur factor.
	 *
	 * @return The main menu's blur factor.
	 */
	public abstract float getBlurFactor();

	/**
	 * The primary colour to be used in UI elements.
	 *
	 * @return The primary colour.
	 */
	public abstract Colour getPrimaryColour();

	/**
	 * Run when disposing the GUI master.
	 */
	public abstract void dispose();

	@Override
	public abstract boolean isActive();
}
