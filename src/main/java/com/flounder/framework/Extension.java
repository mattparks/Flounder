package com.flounder.framework;

import com.flounder.helpers.*;

/**
 * A simple interface that is used to define an extension to the framework. Extensions are used by modules, Example: to use FlounderCamera you must create an extension that implements ICamera.
 *
 * @param <T> The type of module.
 */
public abstract class Extension<T extends com.flounder.framework.Module> {
	private final Class<T> module;
	private Class<T>[] dependencies;
	private boolean initialized;

	/**
	 * Creates a new abstract extension.
	 *
	 * @param module The {@link Module} the extension extends.
	 * @param dependencies Modules the extension depends on.
	 */
	@SafeVarargs
	public Extension(Class<T> module, Class<T>... dependencies) {
		this.module = module;
		this.dependencies = ArrayUtils.addElement(dependencies, module);
		this.initialized = false;

		if (Framework.get() != null && Framework.get().isRunning()) {
			Framework.get().registerModule(Framework.get().loadModule(module)).registerExtension(this);
		}
	}

	/**
	 * Gets the parent module.
	 *
	 * @return The parent module.
	 */
	protected Class<T> getModule() {
		return module;
	}

	/**
	 * Gets all of the dependencies.
	 *
	 * @return The dependencies.
	 */
	protected Class<T>[] getDependencies() {
		return dependencies;
	}

	/**
	 * Gets if the module is initialized.
	 *
	 * @return If the module is initialized.
	 */
	public boolean isInitialized() {
		return initialized;
	}

	/**
	 * Sets if the module is initialized.
	 *
	 * @param initialized If the module is initialized.
	 */
	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

	/**
	 * Gets if the extension is currently active, could be replaced if false.
	 *
	 * @return If the extension is currently active.
	 */
	public abstract boolean isActive();
}
