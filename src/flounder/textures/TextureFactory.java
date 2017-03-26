package flounder.textures;

import flounder.factory.*;
import flounder.logger.*;
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
 * A class that represents a factory for loading textures.
 */
public class TextureFactory extends Factory {
	private static final TextureFactory INSTANCE = new TextureFactory();

	private TextureFactory() {
		super("texture");
	}

	/**
	 * Gets a new builder to be used to create information for build a object from.
	 *
	 * @return A new factory builder.
	 */
	public static TextureBuilder newBuilder() {
		return new TextureBuilder(INSTANCE);
	}

	@Override
	protected FactoryObject newObject() {
		return new TextureObject();
	}

	@Override
	protected void loadData(FactoryObject object, FactoryBuilder builder, String name) {
		TextureBuilder b = (TextureBuilder) builder;
		TextureObject o = (TextureObject) object;

		if (b.getFile() != null) {
			int width = 0;
			int height = 0;
			boolean hasAlpha = false;
			ByteBuffer buffer = null;

			try {
				InputStream in = b.getFile().getInputStream();
				TextureDecoder decoder = new TextureDecoder(in);
				width = decoder.getWidth();
				height = decoder.getHeight();
				hasAlpha = decoder.hasAlpha();
				buffer = ByteBuffer.allocateDirect(4 * width * height);
				decoder.decode(buffer, width * 4, TextureDecoder.Format.BGRA);
				buffer.flip();
				in.close();
			} catch (Exception e) {
				FlounderLogger.error("Tried to load texture '" + b.getFile() + "', didn't work");
				FlounderLogger.exception(e);
				System.exit(-1);
			}

			o.loadData(b.getFile(), buffer, width, height, hasAlpha, b.getNumberOfRows(), name);
		} else if (b.getCubemap() != null) {
			o.loadData(null, null, 0, 0, false, 1, name);
		}
	}

	@Override
	protected void create(FactoryObject object, FactoryBuilder builder) {
		TextureBuilder b = (TextureBuilder) builder;
		TextureObject o = (TextureObject) object;

		if (b.getFile() != null) {
			int textureID = glGenTextures();
			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D, textureID);
			glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, o.getWidth(), o.getHeight(), 0, GL_BGRA, GL_UNSIGNED_BYTE, o.getBuffer());

			if (b.isMipmap()) {
				glGenerateMipmap(GL_TEXTURE_2D);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);

				if (b.isAnisotropic()) {
					glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, 0);
					glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, FlounderTextures.getAnisotropyLevel());
				}
			} else if (b.isNearest()) {
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			} else {
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			}

			if (b.isClampEdges()) {
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
			} else if (b.isClampToBorder()) {
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
				FloatBuffer buffer = BufferUtils.createFloatBuffer(4);
				b.getBorderColour().store(buffer);
				buffer.flip();
				glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, buffer);
			} else {
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
			}

			o.loadGL(textureID, GL_TEXTURE_2D);
		} else if (b.getCubemap() != null) {
			int textureID = glGenTextures();
			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_CUBE_MAP, textureID);
			glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

			for (int i = 0; i < b.getCubemap().length; i++) {
				int width = 0;
				int height = 0;
				boolean hasAlpha = false;
				ByteBuffer buffer = null;

				try {
					InputStream in = b.getCubemap()[i].getInputStream();
					TextureDecoder decoder = new TextureDecoder(in);
					width = decoder.getWidth();
					height = decoder.getHeight();
					hasAlpha = decoder.hasAlpha();
					buffer = ByteBuffer.allocateDirect(4 * width * height);
					decoder.decode(buffer, width * 4, TextureDecoder.Format.BGRA);
					buffer.flip();
					in.close();
				} catch (Exception e) {
					FlounderLogger.error("Tried to load texture '" + b.getCubemap()[i] + "', didn't work");
					FlounderLogger.exception(e);
					System.exit(-1);
				}

				o.setHasAlpha(hasAlpha || o.hasAlpha());
				glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
			}

			glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
			glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
			glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
			o.loadGL(textureID, GL_TEXTURE_CUBE_MAP);
		}
	}

	@Override
	protected Map<String, SoftReference<FactoryObject>> getLoaded() {
		return FlounderTextures.getLoaded();
	}
}
