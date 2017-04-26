package flounder.shaders;

import flounder.factory.*;
import flounder.framework.*;
import flounder.processing.*;
import flounder.profiling.*;
import flounder.resources.*;

import java.lang.ref.*;
import java.util.*;

/**
 * A module used for loading GLSL files into shaders.
 */
public class FlounderShaders extends Module {
	private static final FlounderShaders INSTANCE = new FlounderShaders();
	public static final String PROFILE_TAB_NAME = "Shaders";

	public static final MyFile SHADERS_LOC = new MyFile(MyFile.RES_FOLDER, "shaders");

	private Map<String, SoftReference<FactoryObject>> loaded;

	/**
	 * Creates a new shader loader class.
	 */
	public FlounderShaders() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME, FlounderProcessors.class);
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
	 * Gets a list of loaded shaders.
	 *
	 * @return A list of loaded shaders.
	 */
	public static Map<String, SoftReference<FactoryObject>> getLoaded() {
		return INSTANCE.loaded;
	}

	@Override
	public Module getInstance() {
		return INSTANCE;
	}

	@Override
	public void dispose() {
		loaded.keySet().forEach(key -> ((ShaderObject) loaded.get(key).get()).delete());
		loaded.clear();
	}
}
