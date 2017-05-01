package flounder.models;

import flounder.factory.*;
import flounder.framework.*;
import flounder.loaders.*;
import flounder.processing.*;
import flounder.profiling.*;

import java.lang.ref.*;
import java.util.*;

/**
 * A module used for loading OBJ files into models.
 */
public class FlounderModels extends Module {
	private static final FlounderModels INSTANCE = new FlounderModels();
	public static final String PROFILE_TAB_NAME = "Models";

	private Map<String, SoftReference<FactoryObject>> loaded;

	/**
	 * Creates a new model loader class.
	 */
	public FlounderModels() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME, FlounderLoader.class, FlounderProcessors.class);
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
		this.loaded = new HashMap<>();
	}

	@Handler.Function(Handler.FLAG_UPDATE_PRE)
	public void update() {
	}

	@Handler.Function(Handler.FLAG_PROFILE)
	public void profile() {
		FlounderProfiler.get().add(PROFILE_TAB_NAME, "Loaded", loaded.size());
	}

	/**
	 * Gets a list of loaded models.
	 *
	 * @return A list of loaded models.
	 */
	public static Map<String, SoftReference<FactoryObject>> getLoaded() {
		return INSTANCE.loaded;
	}


	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
		loaded.keySet().forEach(key -> {
			ModelObject model = ((ModelObject) loaded.get(key).get());

			if (model != null && model.isLoaded()) {
				model.delete();
			}
		});
		loaded.clear();
	}
}
