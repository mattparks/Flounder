package com.flounder.factory;

import com.flounder.logger.*;
import com.flounder.processing.*;

import java.lang.ref.*;

/**
 * A builder used to set parameters for loading.
 */
public abstract class FactoryBuilder {
	private Factory factory;

	/**
	 * Creates a new builder.
	 *
	 * @param factory The factory to be used with.
	 */
	protected FactoryBuilder(Factory factory) {
		this.factory = factory;
	}

	/**
	 * Creates a new factory object, carries out the CPU loading, and then runs on the OpenGL thread.
	 *
	 * @return The factory object that has been created.
	 */
	public abstract FactoryObject create();

	/**
	 * Only call from {@link #create()}! Creates the model object.
	 *
	 * @param name The name to be referenced by. This may need to be loaded though the builder.
	 *
	 * @return The factory object that has been created.
	 */
	public FactoryObject builderCreate(String name) {
		SoftReference<FactoryObject> ref = factory.getLoaded().get(name);
		FactoryObject object = ref == null ? null : ref.get();

		if (object == null) {
			if (FlounderLogger.DETAILED) {
				FlounderLogger.get().log(name + " is being loaded into the " + factory.getFactoryName() + " factory right now!");
			}

			factory.getLoaded().remove(name);
			object = factory.newObject();
			FlounderProcessors.get().sendRequest(new FactoryRequestLoad(name, factory, object, this));
			factory.getLoaded().put(name, new SoftReference<>(object));
		}

		return object;
	}
}
