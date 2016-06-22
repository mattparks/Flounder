package flounder.textures;

import flounder.engine.*;
import flounder.resources.*;
import org.lwjgl.*;

import java.io.*;
import java.nio.*;
import java.util.*;

import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * Manages the caches of textures.
 */
public class FlounderTextures implements IModule {
	private List<Integer> textureCache;

	/**
	 * Creates a new texture manager.
	 */
	public FlounderTextures() {
		textureCache = new ArrayList<>();
	}

	@Override
	public void init() {
	}

	@Override
	public void update() {
	}

	@Override
	public void profile() {
		FlounderEngine.getProfiler().add("Textures", "Loaded", textureCache.size());
	}

	/**
	 * Loads a list of textures into a cube map
	 *
	 * @param textureFiles The list of files to load.
	 *
	 * @return The textureID for the cube map.
	 */
	public int loadCubeMap(MyFile... textureFiles) {
		int texID = glGenTextures();
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_CUBE_MAP, texID);
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

		for (int i = 0; i < textureFiles.length; i++) {
			FlounderEngine.getLogger().log(textureFiles[i].getPath() + " is being loaded into the texture cube map!");
			TextureData data = decodeTextureFile(textureFiles[i]);
			glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGBA, data.getWidth(), data.getHeight(), 0, GL_BGRA, GL_UNSIGNED_BYTE, data.getBuffer());
		}

		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		textureCache.add(texID);
		return texID;
	}

	/**
	 * Decodes a texture file into a data class.
	 *
	 * @param file The file to decode.
	 *
	 * @return The decoded data in a class.
	 */
	public TextureData decodeTextureFile(MyFile file) {
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
			FlounderEngine.getLogger().log("Tried to load texture '" + file + "', didn't work");
			FlounderEngine.getLogger().exception(e);
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
	public int loadTextureToOpenGL(TextureData data, TextureBuilder builder) {
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

		textureCache.add(texID);
		return texID;
	}

	/**
	 * Deletes the texture from the cache and OpenGL memory.
	 *
	 * @param textureID The texture to delete.
	 */
	public void deleteTexture(int textureID) {
		textureCache.remove(textureID);
		glDeleteTextures(textureID);
	}

	@Override
	public void dispose() {
		textureCache.forEach(cache -> glDeleteTextures(cache));
		textureCache.clear();
	}
}
