package flounder.engine;

/**
 * A simple interface that can be applied anywhere.
 */
public interface IModule {
	/**
	 * Initializes the module.
	 */
	void init();

	/**
	 * Runs a update of the module.
	 */
	void update();

	/**
	 * Profiles the module.
	 */
	void profile();

	/**
	 * Disposes the module.
	 */
	void dispose();
}
