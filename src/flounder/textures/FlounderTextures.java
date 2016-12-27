package flounder.textures;

import flounder.framework.*;
import flounder.logger.*;
import flounder.processing.*;
import flounder.profiling.*;
import flounder.resources.*;
import org.lwjgl.*;

import java.io.*;
import java.lang.ref.*;
import java.nio.*;
import java.util.*;

import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * A module used for loading and managing OpenGL textures.
 */
public class FlounderTextures extends IModule {
	private static final FlounderTextures instance = new FlounderTextures();

	private static Map<String, SoftReference<Texture>> loaded;
	private List<Integer> textureCache;

	/**
	 * Creates a new OpenGL texture manager.
	 */
	public FlounderTextures() {
		super(ModuleUpdate.UPDATE_PRE, FlounderLogger.class, FlounderProfiler.class, FlounderProcessors.class);
	}

	@Override
	public void init() {
		this.loaded = new HashMap<>();
		this.textureCache = new ArrayList<>();
	}

	@Override
	public void update() {
	}

	@Override
	public void profile() {
		FlounderProfiler.add("Textures", "Loaded", textureCache.size());
	}

	/**
	 * Creates a empty cubemap.
	 *
	 * @param size The size of the cubemaps faces.
	 *
	 * @return A empty cubemap.
	 */
	public static int createEmptyCubeMap(int size) {
		int texID = glGenTextures();
		glBindTexture(GL_TEXTURE_CUBE_MAP, texID);

		for (int i = 0; i < 6; i++) {
			glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGBA8, size, size, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
		}

		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
		glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
		instance.textureCache.add(texID);
		return texID;
	}

	/**
	 * Loads a list of textures into a cube map
	 *
	 * @param textureFiles The list of files to load.
	 *
	 * @return The textureID for the cube map.
	 */
	public static int loadCubeMap(MyFile... textureFiles) {
		int texID = glGenTextures();
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_CUBE_MAP, texID);
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

		for (int i = 0; i < textureFiles.length; i++) {
			FlounderLogger.log(textureFiles[i].getPath() + " is being loaded into the texture cube map!");
			TextureData data = decodeTextureFile(textureFiles[i]);
			glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGBA, data.getWidth(), data.getHeight(), 0, GL_BGRA, GL_UNSIGNED_BYTE, data.getBuffer());
		}

		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
		instance.textureCache.add(texID);
		return texID;
	}

	/**
	 * Decodes a texture file into a data class.
	 *
	 * @param file The file to decode.
	 *
	 * @return The decoded data in a class.
	 */
	public static TextureData decodeTextureFile(MyFile file) {
		int width = 0;
		int height = 0;
		boolean hasAlpha = false;
		ByteBuffer buffer = null;

		try {
			InputStream in = file.getInputStream();
			TextureDecoder decoder = new TextureDecoder(in);
			width = decoder.getWidth();
			height = decoder.getHeight();
			hasAlpha = decoder.hasAlpha();
			buffer = ByteBuffer.allocateDirect(4 * width * height);
			decoder.decode(buffer, width * 4, TextureDecoder.Format.BGRA);
			buffer.flip();
			in.close();
		} catch (Exception e) {
			FlounderLogger.error("Tried to load texture '" + file + "', didn't work");
			FlounderLogger.exception(e);
			System.exit(-1);
		}

		return new TextureData(buffer, width, height, hasAlpha);
	}

	/**
	 * Loads decoded texture data and builder parameters into a OpenGL texture.
	 *
	 * @param data Decoded texture data.
	 * @param builder The builder with parameters.
	 *
	 * @return A OpenGL texture ID.
	 */
	public static int loadTextureToOpenGL(TextureData data, TextureBuilder builder) {
		int texID = glGenTextures();
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, texID);
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, data.getWidth(), data.getHeight(), 0, GL_BGRA, GL_UNSIGNED_BYTE, data.getBuffer());

		if (builder.isMipmap()) {
			glGenerateMipmap(GL_TEXTURE_2D);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);

			if (builder.isAnisotropic()) {
				glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, 0);
				float maxAnisotropy = glGetFloat(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT);
				glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, maxAnisotropy);
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
			FloatBuffer buffer = BufferUtils.createFloatBuffer(4);
			builder.getBorderColour().store(buffer);
			buffer.flip();
			glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, buffer);
		} else {
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		}

		instance.textureCache.add(texID);
		return texID;
	}

	/**
	 * Deletes the texture from the cache and OpenGL memory.
	 *
	 * @param textureID The texture to delete.
	 */
	public static void deleteTexture(int textureID) {
		if (instance.textureCache.contains(textureID)) {
			instance.textureCache.remove((Integer) textureID);
			glDeleteTextures(textureID);
		}
	}

	/**
	 * Gets a list of loaded shaders.
	 *
	 * @return A list of loaded shaders.
	 */
	public static Map<String, SoftReference<Texture>> getLoaded() {
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

		textureCache.forEach(cache -> glDeleteTextures(cache));
		textureCache.clear();
	}
}
