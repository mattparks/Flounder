package flounder.textures;

import flounder.engine.*;
import flounder.resources.*;
import org.lwjgl.*;
import org.lwjgl.opengl.*;

import java.io.*;
import java.nio.*;
import java.util.*;

/**
 * Manages the caches of textures.
 */
public class TextureManager {
	private static List<Integer> textureCache = new ArrayList<>();

	/**
	 * Loads a list of textures into a cube map
	 *
	 * @param textureFiles The list of files to load.
	 *
	 * @return The textureID for the cube map.
	 */
	public static int loadCubeMap(final MyFile... textureFiles) {
		final int texID = GL11.glGenTextures();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texID);
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

		for (int i = 0; i < textureFiles.length; i++) {
			FlounderLogger.log(textureFiles[i].getPath() + " is being loaded into the texture cube map!");
			TextureData data = decodeTextureFile(textureFiles[i]);
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, data.getWidth(), data.getHeight(), 0, GL12.GL_BGRA, GL11.GL_UNSIGNED_BYTE, data.getBuffer());
		}

		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		textureCache.add(texID);
		return texID;
	}

	protected static TextureData decodeTextureFile(final MyFile file) {
		int width = 0;
		int height = 0;
		ByteBuffer buffer = null;

		try {
			final InputStream in = file.getInputStream();
			final TextureDecoder decoder = new TextureDecoder(in);
			width = decoder.getWidth();
			height = decoder.getHeight();
			buffer = ByteBuffer.allocateDirect(4 * width * height);
			decoder.decode(buffer, width * 4, TextureDecoder.Format.BGRA);
			buffer.flip();
			in.close();
		} catch (Exception e) {
			FlounderLogger.log("Tried to load texture '" + file + "', didn't work");
			FlounderLogger.exception(e);
			System.exit(-1);
		}

		return new TextureData(buffer, width, height);
	}

	protected static int loadTextureToOpenGL(final TextureData data, final TextureBuilder builder) {
		final int texID = GL11.glGenTextures();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texID);
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, data.getWidth(), data.getHeight(), 0, GL12.GL_BGRA, GL11.GL_UNSIGNED_BYTE, data.getBuffer());

		if (builder.isMipmap()) {
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);

			if (builder.isAnisotropic()) {
				GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0);
				final float maxAnisotropy = GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT);
				GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, maxAnisotropy);
			}
		} else if (builder.isNearest()) {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		} else {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		}

		if (builder.isClampEdges()) {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		} else if (builder.isClampToBorder()) {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL13.GL_CLAMP_TO_BORDER);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL13.GL_CLAMP_TO_BORDER);
			final FloatBuffer buffer = BufferUtils.createFloatBuffer(4);
			builder.getBorderColour().store(buffer);
			buffer.flip();
			GL11.glTexParameterfv(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_BORDER_COLOR, buffer);
		} else {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		}

		textureCache.add(texID);
		return texID;
	}

	protected static void deleteTexture(final Integer textureID) {
		textureCache.remove(textureID);
		GL11.glDeleteTextures(textureID);
	}

	/**
	 * Deletes all of the textures in the cache.
	 */
	public static void dispose() {
		textureCache.forEach(GL11::glDeleteTextures);
	}
}
