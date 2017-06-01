package flounder.textures;

import flounder.factory.*;
import flounder.framework.*;
import flounder.loaders.*;
import flounder.platform.*;
import flounder.processing.*;

import java.lang.ref.*;
import java.util.*;

/**
 * A module used for loading texture files.
 */
public class FlounderTextures extends Module {
	private Map<String, SoftReference<FactoryObject>> loaded;

	private float anisotropyLevel = -1;

	/**
	 * Creates a new texture loader class.
	 */
	public FlounderTextures() {
		super(FlounderLoader.class, FlounderProcessors.class);
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
		this.loaded = new HashMap<>();

		float maxAnisotropy = FlounderPlatform.get().getMaxAnisotropy();

		if (anisotropyLevel == -1 || anisotropyLevel > maxAnisotropy) {
			anisotropyLevel = maxAnisotropy;
		}
	}

	@Handler.Function(Handler.FLAG_UPDATE_PRE)
	public void update() {
	}

	/**
	 * Gets a list of loaded textures.
	 *
	 * @return A list of loaded textures.
	 */
	public Map<String, SoftReference<FactoryObject>> getLoaded() {
		return this.loaded;
	}

	/**
	 * Loads a texture into memory.
	 *
	 * @param builder The builder to load from.
	 * @param object The object to load to.
	 */
	@Module.MethodReplace
	public void loadTexture(TextureBuilder builder, TextureObject object) {
	}

	/**
	 * Deletes a texture from memory.
	 *
	 * @param textureID The texture to delete.
	 */
	@Module.MethodReplace
	public void deleteTexture(int textureID) {
	}

	/**
	 * Gets the current anisotropy level for textures with anisotropy enabled to use.
	 *
	 * @return The current anisotropy level.
	 */
	@Module.MethodReplace
	public float getAnisotropyLevel() {
		return 0.0f;
	}

	@Module.MethodReplace
	public void setAnisotropyLevel(float anisotropyLevel) {
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
		loaded.keySet().forEach(key -> ((TextureObject) loaded.get(key).get()).delete());
		loaded.clear();
	}

	@Module.Instance
	public static FlounderTextures get() {
		return (FlounderTextures) Framework.getInstance(FlounderTextures.class);
	}

	@Module.TabName
	public static String getTab() {
		return "Textures";
	}
}
