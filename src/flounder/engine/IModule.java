package flounder.engine;

/**
 * A simple interface that can be applied anywhere.
 */
public abstract class IModule<T extends IModule> implements Runnable {
	private final Class<T>[] requires;
	private ModuleUpdate moduleUpdate;
	private boolean initialized;

	/**
	 * Creates a new abstract module.
	 *
	 * @param moduleUpdate How/when the module will update.
	 * @param requires Classes the module depends on.
	 */
	public IModule(ModuleUpdate moduleUpdate, Class<T>... requires) {
		this.requires = requires;
		this.moduleUpdate = moduleUpdate;
		this.initialized = false;
	}

	/**
	 * Initializes the module.
	 */
	public abstract void init();

	/**
	 * Runs a update of the module.
	 */
	@Override
	public abstract void run();

	/**
	 * Profiles the module.
	 */
	public abstract void profile();

	/**
	 * Gets the classes that the module requires.
	 *
	 * @return The classes that the module requires.
	 */
	protected Class<T>[] getRequires() {
		return requires;
	}

	/**
	 * Gets how/when the module will update.
	 *
	 * @return How/when the module will update.
	 */
	public ModuleUpdate getModuleUpdate() {
		return moduleUpdate;
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
	 * Gets the current module instance.
	 *
	 * @return The current module instance.
	 */
	public abstract IModule getInstance();

	/**
	 * Disposes the module.
	 */
	public abstract void dispose();

	/**
	 * A enum that defines where a module will update.
	 */
	public enum ModuleUpdate {
		ALWAYS, BEFORE_ENTRANCE, AFTER_ENTRANCE
	}
}
