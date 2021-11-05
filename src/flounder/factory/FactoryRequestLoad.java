package flounder.factory;

import flounder.logger.*;
import flounder.maths.*;
import flounder.processing.opengl.*;
import flounder.processing.resource.*;

/**
 * A class that can process a request to load a factory object.
 */
public class FactoryRequestLoad implements RequestResource, RequestOpenGL {
	private String name;
	private Factory factory;
	private FactoryObject object;
	private FactoryBuilder builder;

	/**
	 * Creates a new factory load request.
	 *
	 * @param name The name of the object being loaded.
	 * @param factory The factory to use when executing requests.
	 * @param builder The builder to load from.
	 * @param object The object to load into.
	 */
	protected FactoryRequestLoad(String name, Factory factory, FactoryObject object, FactoryBuilder builder) {
		this.name = name;
		this.factory = factory;
		this.object = object;
		this.builder = builder;
	}

	@Override
	public void executeRequestResource() {
		// Loads resource data into the object.
		factory.loadData(object, builder, name);
	}

	@Override
	public void executeRequestGL() {
		Timer timer = null;

		while (object == null || !object.isDataLoaded()) {
			if (timer == null) {
				FlounderLogger.get().warning("Factory request for " + name + " is waiting for data!");
				timer = new Timer(5.0); // Waits 5 seconds for data, if it stays unloaded then this object is not loaded.
			}

			if (timer.isPassedTime()) {
				if (object != null) {
					object.setDataLoaded(false);
					object.setFullyLoaded(false);
				}

				timer.resetStartTime();
				FlounderLogger.get().error("Factory request for " + name + " failed! The object will not be loaded!");
			}

			// Wait for resources to load into data...
		}

		// Creates the object and sets as loaded.
		factory.create(object, builder);
	}
}