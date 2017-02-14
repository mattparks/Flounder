package flounder.models;

import flounder.factory.*;
import flounder.framework.*;
import flounder.loaders.*;
import flounder.logger.*;
import flounder.processing.*;
import flounder.profiling.*;

import java.lang.ref.*;
import java.util.*;

/**
 * A module used for loading materials for meshes.
 */
public class FlounderModels extends IModule {
	private static final FlounderModels INSTANCE = new FlounderModels();
	public static final String PROFILE_TAB_NAME = "Models";

	private Map<String, SoftReference<FactoryObject>> loaded;

	/**
	 * Creates a new material loader class.
	 */
	public FlounderModels() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME, FlounderLogger.class, FlounderLoader.class, FlounderProcessors.class);
	}

	@Override
	public void init() {
		this.loaded = new HashMap<>();
	}

	@Override
	public void update() {
	}

	@Override
	public void profile() {
		FlounderProfiler.add(PROFILE_TAB_NAME, "Loaded", loaded.size());
	}

	/**
	 * Gets a list of loaded models.
	 *
	 * @return A list of loaded models.
	 */
	public static Map<String, SoftReference<FactoryObject>> getLoaded() {
		return INSTANCE.loaded;
	}

	@Override
	public IModule getInstance() {
		return INSTANCE;
	}

	@Override
	public void dispose() {
		loaded.keySet().forEach(key -> ((ModelObject) loaded.get(key).get()).delete());
		loaded.clear();
	}
}
