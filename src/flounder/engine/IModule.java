package flounder.engine;

/**
 * A simple interface that can be applied anywhere.
 */
public abstract class IModule<T extends IModule> {
	private final Class<T>[] requires;
	private boolean initialized;

	/**
	 * Creates a new abstract module.
	 *
	 * @param requires Classes the module depends on.
	 */
	public IModule(Class<T>... requires) {
		this.requires = requires;
		this.initialized = false;
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
	protected Class<T>[] getRequires() {
		return requires;
	}

	/**
	 * Gets if the module is initialized.
	 *
	 * @return If the module is initialized.
	 */
	protected boolean isInitialized() {
		return initialized;
	}

	/**
	 * Sets if the module is initialized.
	 *
	 * @param initialized If the module is initialized.
	 */
	protected void setInitialized(boolean initialized) {
		this.initialized = initialized;
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
