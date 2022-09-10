package com.flounder.framework;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * A simple interface used for defining framework modules.
 *
 * @param <T> The type of module.
 */
public class Module<T extends com.flounder.framework.Module> {
	private Class<T>[] dependencies;
	private List<Handler> handlers;
	private List<Extension> extensions;

	private boolean extensionChange;

	/**
	 * Creates a new module object.
	 *
	 * @param dependencies The list of module classes this module depends on.
	 */
	@SafeVarargs
	public Module(Class<T>... dependencies) {
		this.dependencies = dependencies;
		this.handlers = new ArrayList<>();
		this.extensions = new ArrayList<>();

		this.extensionChange = true;

		for (Method method : this.getClass().getDeclaredMethods()) {
			Handler.Function function = method.getAnnotation(Handler.Function.class);

			if (function != null) {
				this.handlers.add(new Handler(function.value(), method, this));
			}
		}
	}

	/**
	 * Gets all of the handlers.
	 *
	 * @return The handlers.
	 */
	protected List<Handler> getHandlers() {
		return handlers;
	}

	protected Handler getHandler(int flag) {
		for (Handler handler : handlers) {
			if (handler.getFlag() == flag) {
				return handler;
			}
		}

		return null;
	}

	protected void runHandler(int flag) {
		for (Handler handler : handlers) {
			if (handler.getFlag() == flag) {
				handler.run();
			}
		}
	}

	protected boolean hasHandlerRun(int flag) {
		for (Handler handler : handlers) {
			if (handler.getFlag() == flag) {
				return handler.hasRun();
			}
		}

		return false;
	}

	public void registerHandler(Handler handler) {
		this.handlers.add(handler);
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
	 * Gets all of the extensions.
	 *
	 * @return The extensions.
	 */
	protected List<Extension> getExtensions() {
		return extensions;
	}

	/**
	 * Registers an extension with a module.
	 *
	 * @param extension The extension to init.
	 */
	protected void registerExtension(Extension extension) {
		if (!extensions.contains(extension)) {
			Framework.get().registerModules(Framework.get().loadModules(extension.getDependencies()));
			extensions.add(extension);
			extensionChange = true;
		}
	}

	/**
	 * Registers extensions with a module.
	 *
	 * @param extensions The extensions to init.
	 */
	protected void registerExtensions(Extension... extensions) {
		for (Extension extension : extensions) {
			registerExtension(extension);
		}
	}

	/**
	 * Finds a new extension for this module that implements an interface/class. Call {@link #cancelChange()} looking for all type changes.
	 *
	 * @param last The last object to compare to.
	 * @param type The class type of object to find a extension that matches for.
	 * @param onlyRunOnChange When this and {@link #extensionChange} is true, this will update a check, otherwise a object will not be checked for (returning null).
	 * @param <Y> The type of extension class to be found.
	 *
	 * @return The found extension to be active and matched the types provided.
	 */
	public <Y> Extension getExtensionMatch(Extension last, Class<Y> type, boolean onlyRunOnChange) {
		if ((onlyRunOnChange && !extensionChange) || extensions.isEmpty()) {
			return null;
		}

		for (Extension extension : extensions) {
			if (extension.isActive() && type.isInstance(extension) && !extension.equals(last)) {
				return extension;
			}
		}

		this.extensionChange = false;
		return null;
	}

	/**
	 * Finds a list of new extensions for this module that implements an interface/class. Call {@link #cancelChange()} looking for all type changes.
	 *
	 * @param last The last list of object to compare to.
	 * @param type The class type of object to find a extension that matches for.
	 * @param onlyRunOnChange When this and {@link #extensionChange} is true, this will update a check, otherwise a object will not be checked for (returning null).
	 * @param <Y> The type of extension classes to be found.
	 *
	 * @return The found extensions to be active and matched the types provided.
	 */
	public <Y> List<Extension> getExtensionMatches(List<Extension> last, Class<Y> type, boolean onlyRunOnChange) {
		if ((onlyRunOnChange && !extensionChange) || extensions.isEmpty()) {
			return null;
		}

		List<Extension> results = new ArrayList<>();

		for (Extension extension : extensions) {
			if (extension.isActive() && type.isInstance(extension) && !extension.equals(last)) {
				results.add(extension);
			}
		}

		if (results.equals(last)) {
			return null;
		}

		this.extensionChange = false;
		return results;
	}

	/**
	 * Gets if the extensions list has changed.
	 *
	 * @return If the extensions list has changed.
	 */
	public boolean hasExtensionChanged() {
		return extensionChange;
	}

	public void cancelChange() {
		this.extensionChange = false;
	}

	public Module getInstance() {
		Module override = Framework.get().getOverride(this.getClass());
		Module actual = Framework.get().getModule(this.getClass());

		if (actual == null) {
			actual = this;
		}

		return override == null ? actual : override;
	}

	/**
	 * Represents a class that overrides a existing module.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface ModuleOverride {
	}

	/**
	 * Represents a method that has to be implemented by a module override.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface MethodReplace {
	}

	/**
	 * Represents a method that gets the instance to a module.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface Instance {
	}
}
