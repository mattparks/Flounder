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
	private Map<String, SoftReference<FactoryObject>> loaded;

	/**
	 * Creates a new model loader class.
	 */
	public FlounderModels() {
		super(FlounderLoader.class, FlounderProcessors.class);
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
		FlounderProfiler.get().add(getTab(), "Loaded", loaded.size());
	}

	/**
	 * Gets a list of loaded models.
	 *
	 * @return A list of loaded models.
	 */
	public Map<String, SoftReference<FactoryObject>> getLoaded() {
		return this.loaded;
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

	@Module.Instance
	public static FlounderModels get() {
		return (FlounderModels) Framework.getInstance(FlounderModels.class);
	}

	@Module.TabName
	public static String getTab() {
		return "Models";
	}
}
