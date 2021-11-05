package flounder.textures;

import flounder.factory.*;
import flounder.framework.*;
import flounder.loaders.*;
import flounder.logger.FlounderLogger;
import flounder.platform.*;
import flounder.processing.*;

import java.io.InputStream;
import java.lang.ref.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.*;

import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL12.GL_BGRA;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL14.GL_TEXTURE_LOD_BIAS;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

/**
 * A module used for loading texture files.
 */
public class FlounderTextures extends flounder.framework.Module {
	private Map<String, SoftReference<FactoryObject>> loaded;

	private float anisotropyLevel = -1;

	/**
	 * Creates a new texture loader class.
	 */
	public FlounderTextures() {
		this(-1);
	}

	public FlounderTextures(float anisotropyLevel) {
		super(FlounderLoader.class, FlounderProcessors.class);
		this.anisotropyLevel = anisotropyLevel;
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
	public void loadTexture(TextureBuilder builder, TextureObject object) {
		if (builder.getFile() != null) {
			int textureID = glGenTextures();
			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D, textureID);
			glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, object.getWidth(), object.getHeight(), 0, GL_BGRA, GL_UNSIGNED_BYTE, object.getBuffer());

			if (builder.isMipmap()) {
				glGenerateMipmap(GL_TEXTURE_2D);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);

				if (builder.isAnisotropic()) {
					glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, 0);
					glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, FlounderTextures.get().getAnisotropyLevel());
				}
			} else if (builder.isNearest()) {
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			} else {
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			}

			if (builder.isClampEdges()) {
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
			} else if (builder.isClampToBorder()) {
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
				FloatBuffer buffer = FlounderPlatform.get().createFloatBuffer(4);
				builder.getBorderColour().store(buffer);
				buffer.flip();
				glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, buffer);
			} else {
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
			}

			object.loadGL(textureID, GL_TEXTURE_2D);
		} else if (builder.getCubemap() != null) {
			int textureID = glGenTextures();
			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_CUBE_MAP, textureID);
			glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

			for (int i = 0; i < builder.getCubemap().length; i++) {
				int width = 0;
				int height = 0;
				boolean hasAlpha = false;
				ByteBuffer buffer = null;

				try {
					InputStream in = builder.getCubemap()[i].getInputStream();
					TextureDecoder decoder = new TextureDecoder(in);
					width = decoder.getWidth();
					height = decoder.getHeight();
					hasAlpha = decoder.hasAlpha();
					buffer = ByteBuffer.allocateDirect(4 * width * height);
					decoder.decode(buffer, width * 4, TextureDecoder.Format.BGRA);
					buffer.flip();
					in.close();
				} catch (Exception e) {
					FlounderLogger.get().error("Tried to load texture '" + builder.getCubemap()[i] + "', didn't work");
					FlounderLogger.get().exception(e);
					System.exit(-1);
				}

				object.setHasAlpha(hasAlpha || object.hasAlpha());
				glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
			}

			glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
			glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
			glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
			object.loadGL(textureID, GL_TEXTURE_CUBE_MAP);
		}
	}

	/**
	 * Deletes a texture from memory.
	 *
	 * @param textureID The texture to delete.
	 */
	public void deleteTexture(int textureID) {
		glDeleteTextures(textureID);
	}

	/**
	 * Gets the current anisotropy level for textures with anisotropy enabled to use.
	 *
	 * @return The current anisotropy level.
	 */
	public float getAnisotropyLevel() {
		return anisotropyLevel;
	}

	public void setAnisotropyLevel(float anisotropyLevel) {
		this.anisotropyLevel = anisotropyLevel;
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
		loaded.keySet().forEach(key -> ((TextureObject) loaded.get(key).get()).delete());
		loaded.clear();
	}

	@flounder.framework.Module.Instance
	public static FlounderTextures get() {
		return (FlounderTextures) Framework.get().getModule(FlounderTextures.class);
	}
}
