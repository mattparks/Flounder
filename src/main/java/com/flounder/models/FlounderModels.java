package com.flounder.models;

import com.flounder.factory.*;
import com.flounder.framework.*;
import com.flounder.loaders.*;
import com.flounder.processing.*;

import java.lang.ref.*;
import java.util.*;

/**
 * A module used for loading OBJ files into models.
 */
public class FlounderModels extends com.flounder.framework.Module {
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

	@com.flounder.framework.Module.Instance
	public static FlounderModels get() {
		return (FlounderModels) Framework.get().getModule(FlounderModels.class);
	}
}
