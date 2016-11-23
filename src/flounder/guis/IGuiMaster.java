package flounder.guis;

/**
 * A interface used to manage a main GUI system.
 */
public interface IGuiMaster {
	/**
	 * Creates the main GUI system.
	 */
	void init();

	/**
	 * Checks inputs and updates Guis.
	 */
	void update();

	/**
	 * Gets if the main menu is open.
	 *
	 * @return If the main menu is open.
	 */
	boolean isMenuIsOpen();

	/**
	 * Forces the main GUI to open.
	 */
	void openMenu();

	/**
	 * Gets the main menu's blur factor.
	 *
	 * @return The main menu's blur factor.
	 */
	float getBlurFactor();

	/**
	 * Gets if the gui manager is currently active, will disable the gui manager if false.
	 *
	 * @return If the gui manager is currently active.
	 */
	boolean isActive();

	/**
	 * Cleans up all of the gui manager objects processes. Should be called when the game closes.
	 */
	void dispose();
}
