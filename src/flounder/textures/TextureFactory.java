package flounder.textures;

import flounder.factory.*;
import flounder.logger.*;

import java.io.*;
import java.lang.ref.*;
import java.nio.*;
import java.util.*;

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
		FlounderTextures.loadTexture(b, o);
	}

	@Override
	protected Map<String, SoftReference<FactoryObject>> getLoaded() {
		return FlounderTextures.getLoaded();
	}
}
