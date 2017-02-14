package flounder.textures;

import flounder.factory.*;
import flounder.framework.*;
import flounder.loaders.*;
import flounder.processing.*;
import flounder.profiling.*;

import java.lang.ref.*;
import java.util.*;

import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * A module used for loading texture files.
 */
public class FlounderTextures extends IModule {
	private static final FlounderTextures INSTANCE = new FlounderTextures();
	public static final String PROFILE_TAB_NAME = "Textures";

	private Map<String, SoftReference<FactoryObject>> loaded;

	private float anisotropyLevel = -1;

	/**
	 * A function called before initialization to configure the textures.
	 *
	 * @param anisotropyLevel The new anisotropy target level.
	 */
	public static void setup(float anisotropyLevel) {
		INSTANCE.anisotropyLevel = anisotropyLevel;
	}

	/**
	 * Creates a new model loader class.
	 */
	public FlounderTextures() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME, FlounderLoader.class, FlounderProcessors.class);
	}

	@Override
	public void init() {
		this.loaded = new HashMap<>();

		float maxAnisotropy = glGetFloat(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT);

		if (anisotropyLevel == -1 || anisotropyLevel > maxAnisotropy) {
			anisotropyLevel = maxAnisotropy;
		}
	}

	@Override
	public void update() {
	}

	@Override
	public void profile() {
		FlounderProfiler.add(PROFILE_TAB_NAME, "Loaded", loaded.size());
		FlounderProfiler.add(PROFILE_TAB_NAME, "Max Anisotropy", glGetFloat(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
	}

	/**
	 * Gets a list of loaded models.
	 *
	 * @return A list of loaded models.
	 */
	public static Map<String, SoftReference<FactoryObject>> getLoaded() {
		return INSTANCE.loaded;
	}

	/**
	 * Gets the current anisotropy level for textures with anisotropy enabled to use.
	 *
	 * @return The current anisotropy level.
	 */
	public static float getAnisotropyLevel() {
		return INSTANCE.anisotropyLevel;
	}

	@Override
	public IModule getInstance() {
		return INSTANCE;
	}

	@Override
	public void dispose() {
		loaded.keySet().forEach(key -> ((TextureObject) loaded.get(key).get()).delete());
		loaded.clear();
	}
}
