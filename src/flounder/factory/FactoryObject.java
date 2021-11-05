package flounder.factory;

/**
 * The object the factory will be loading into.
 */
public abstract class FactoryObject {
	private boolean dataLoaded;
	private boolean fullyLoaded;

	/**
	 * Creates a new empty factory object.
	 */
	public FactoryObject() {
		dataLoaded = false;
		fullyLoaded = false;
	}

	/**
	 * Gets if the data has been loaded into the object.
	 *
	 * @return If the object is loaded.
	 */
	protected boolean isDataLoaded() {
		return dataLoaded;
	}

	/**
	 * Sets that the factory has data loaded.
	 *
	 * @param dataLoaded If the data has been loaded.
	 */
	protected void setDataLoaded(boolean dataLoaded) {
		this.dataLoaded = dataLoaded;
	}

	/**
	 * Gets if the information has been loaded into the object.
	 *
	 * @return If the object is loaded.
	 */
	public boolean isLoaded() {
		return fullyLoaded;
	}

	/**
	 * Sets that the factory has been loaded.
	 *
	 * @param fullyLoaded If the factory has been loaded.
	 */
	protected void setFullyLoaded(boolean fullyLoaded) {
		this.fullyLoaded = fullyLoaded;
	}
}
