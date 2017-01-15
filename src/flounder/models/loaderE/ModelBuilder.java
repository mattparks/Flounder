package flounder.models.loaderE;

import flounder.factory.*;
import flounder.resources.*;

public class ModelBuilder extends FactoryBuilder {
	private MyFile file;

	public ModelBuilder(Factory factory) {
		super(factory);
		this.file = null;
	}

	public ModelBuilder setFile(MyFile file) {
		this.file = file;
		this.setName(file.getPath());
		return this;
	}
}
