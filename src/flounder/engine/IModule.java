package flounder.engine;

import java.util.*;

/**
 * A simple interface that can be applied anywhere.
 */
public abstract class IModule<T extends IModule> {
	private final Class<T>[] requires;
	public List<IModule> usedby;

	/**
	 * Creates a new abstract module.
	 *
	 * @param requires Classes the module depends on.
	 */
	public IModule(Class<T>... requires) {
		this.requires = requires;
		this.usedby = new ArrayList<>();
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
