package flounder.models.loaderE;

import flounder.factory.*;

import java.lang.ref.*;
import java.util.*;

public class ModelFactory extends Factory {
	private static final Map<String, SoftReference<FactoryObject>> LOADED = new HashMap<>();

	public ModelFactory() {
		super("model");
	}

	@Override
	public ModelBuilder newBuilder() {
		return new ModelBuilder(this);
	}

	@Override
	public ModelObject newObject() {
		return new ModelObject();
	}

	@Override
	public void loadData(FactoryObject object, FactoryBuilder builder) {
		// TODO: Load resource data.
	}

	@Override
	protected void create(FactoryObject object) {
		// TODO: Load resources to OpenGL.
	}

	@Override
	public Map<String, SoftReference<FactoryObject>> getLoaded() {
		return LOADED;
	}
}
