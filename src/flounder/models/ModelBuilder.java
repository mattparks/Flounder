package flounder.models;

import flounder.factory.*;
import flounder.resources.*;

public class ModelBuilder extends FactoryBuilder {
	private MyFile file;
	private ModelData manual;

	public ModelBuilder(Factory factory) {
		super(factory);
		this.file = null;
		this.manual = null;
	}

	public ModelBuilder setFile(MyFile file) {
		this.file = file;
		return this;
	}

	public ModelBuilder setManual(ModelData manual) {
		this.manual = manual;
		return this;
	}

	public MyFile getFile() {
		return file;
	}

	public ModelData getManual() {
		return manual;
	}

	@Override
	public ModelObject create() {
		if (manual != null) {
			return (ModelObject) builderCreate(manual.getName());
		} else if (file != null) {
			return (ModelObject) builderCreate(file.getName());
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
