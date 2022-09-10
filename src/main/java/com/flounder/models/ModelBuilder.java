package com.flounder.models;

import com.flounder.factory.*;
import com.flounder.resources.*;

/**
 * A builder used to set model parameters for loading.
 */
public class ModelBuilder extends FactoryBuilder {
	private MyFile file;
	private ModelLoadManual manual;

	protected ModelBuilder(Factory factory) {
		super(factory);
		this.file = null;
		this.manual = null;
	}

	/**
	 * Sets the models source file.
	 *
	 * @param file The source file.
	 *
	 * @return this.
	 */
	public ModelBuilder setFile(MyFile file) {
		this.file = file;
		return this;
	}

	/**
	 * Sets the models manual source data.
	 *
	 * @param manual The manual source data.
	 *
	 * @return this.
	 */
	public ModelBuilder setManual(ModelLoadManual manual) {
		this.manual = manual;
		return this;
	}

	/**
	 * Gets the source file.
	 *
	 * @return The source file.
	 */
	public MyFile getFile() {
		return file;
	}

	/**
	 * Gets the manual source data.
	 *
	 * @return The manual source data.
	 */
	public ModelLoadManual getManual() {
		return manual;
	}

	@Override
	public ModelObject create() {
		if (manual != null) {
			return (ModelObject) builderCreate(manual.getName());
		} else if (file != null) {
			return (ModelObject) builderCreate(file.getPath());
		}

		return null;
	}

	@Override
	public String toString() {
		return "ModelBuilder{" +
				"file=" + file +
				", manual=" + manual +
				'}';
	}
}
