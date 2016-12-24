package flounder.models.animation;

import flounder.framework.*;
import flounder.logger.*;
import flounder.processing.*;
import flounder.resources.*;

import java.lang.ref.*;
import java.util.*;

/**
 * A module used for loading and managing models.
 */
public class FlounderAnimations extends IModule {
	private static final FlounderAnimations instance = new FlounderAnimations();

	private Map<String, SoftReference<Animation>> loaded;

	/**
	 * Creates a new model loader class.
	 */
	public FlounderAnimations() {
		super(ModuleUpdate.UPDATE_PRE, FlounderLogger.class, FlounderProcessors.class);
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
	}

	/**
	 * Loads a OBJ file into a ModelRaw object.
	 *
	 * @param file The file to be loaded.
	 *
	 * @return The data loaded.
	 */
	public static AnimationData loadAnimation(MyFile file) {
		// TODO: Load to real data!
		return new AnimationData(file);
	}

	/**
	 * Gets a list of loaded models.
	 *
	 * @return A list of loaded models.
	 */
	public static Map<String, SoftReference<Animation>> getLoaded() {
		return instance.loaded;
	}

	@Override
	public IModule getInstance() {
		return instance;
	}

	@Override
	public void dispose() {
		loaded.keySet().forEach(key -> loaded.get(key).get().delete());
		loaded.clear();
	}
}
