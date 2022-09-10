package com.flounder.factory;

import java.lang.ref.*;
import java.util.*;

/**
 * A class that represents a factory and its basic functionality.
 */
public abstract class Factory {
	private String factoryName;

	/**
	 * Creates a new factory object.
	 *
	 * @param factoryName The name of the factory.
	 */
	public Factory(String factoryName) {
		this.factoryName = factoryName;
	}

	/**
	 * Creates a new empty object of the factories type.
	 *
	 * @return The new object.
	 */
	protected abstract FactoryObject newObject();

	/**
	 * Used to load resource data into a factory object, using building parameters.
	 *
	 * @param object The object to load data into.
	 * @param builder The builder to use parameters from.
	 * @param name The name of the object being loaded.
	 */
	protected abstract void loadData(FactoryObject object, FactoryBuilder builder, String name);

	/**
	 * Used to take resource data and turn it into the object. Can be used to load into OpenGL.
	 *
	 * @param object The object to create for.
	 * @param builder The builder to use parameters from.
	 */
	protected abstract void create(FactoryObject object, FactoryBuilder builder);

	/**
	 * Gets a static list of all loaded factory objects. This is used to hold a list of already loaded objects to reduce load requests.
	 *
	 * @return The map of loaded factory objects, mapped by name.
	 */
	protected abstract Map<String, SoftReference<FactoryObject>> getLoaded();

	/**
	 * The name of the factory.
	 *
	 * @return The factories name.
	 */
	protected String getFactoryName() {
		return factoryName;
	}
}
