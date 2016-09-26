package flounder.engine;

/**
 * A simple interface that can be applied anywhere.
 */
public abstract class IModule {
	private final Class[] requires;

	/**
	 * Creates a new abstract module.
	 *
	 * @param requires Classes the module depends on.
	 */
	public IModule(Class... requires) {
		this.requires = requires;
		FlounderEngine.registerModule(this);
	}

	/**
	 * Initializes the module.
	 */
	public abstract void init();

	/**
	 * Runs a update of the module.
	 */
	public abstract void update();

	/**
	 * Gets the classes that the module requires.
	 *
	 * @return The classes that the module requires.
	 */
	public Class[] getRequires() {
		return requires;
	}

	/**
	 * Profiles the module.
	 */
	public abstract void profile();

	/**
	 * Disposes the module.
	 */
	public abstract void dispose();
}
