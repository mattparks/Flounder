package flounder.engine;

/**
 * A simple interface that can be applied anywhere.
 */
public abstract class IModule<T extends IModule> {
	private final Class<T>[] requires;

	/**
	 * Creates a new abstract module.
	 *
	 * @param isInstance If this object is the instance for the module type.
	 * @param requires Classes the module depends on.
	 */
	public IModule(boolean isInstance, Class<T>... requires) {
		this.requires = requires;

		// Only register if this instance is the same of the modules instance.
		if (isInstance) {
			FlounderEngine.registerModule(this);
		}
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
	public Class<T>[] getRequires() {
		return requires;
	}

	/**
	 * Profiles the module.
	 */
	public abstract void profile();

	/**
	 * Gets the current module instance.
	 *
	 * @return The current module instance.
	 */
	public abstract IModule getInstance();

	/**
	 * Disposes the module.
	 */
	public abstract void dispose();
}
