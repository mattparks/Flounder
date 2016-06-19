package flounder.textures;

import java.nio.*;

/**
 * A class that represents data that was loaded from {@link flounder.textures.TextureDecoder}.
 */
public class TextureData {
	private ByteBuffer buffer;
	private int width;
	private int height;
	private boolean hasAlpha;

	/**
	 * Creates a new data holder.
	 *
	 * @param buffer The textures data in byte buffer form.
	 * @param width The textures width.
	 * @param height The textures height.
	 * @param hasAlpha If the texture has a alpha channel.
	 */
	protected TextureData(ByteBuffer buffer, int width, int height, boolean hasAlpha) {
		this.buffer = buffer;
		this.width = width;
		this.height = height;
		this.hasAlpha = hasAlpha;
	}

	/**
	 * Gets the textures loaded buffer.
	 *
	 * @return The textures loaded buffer.
	 */
	protected ByteBuffer getBuffer() {
		return buffer;
	}

	/**
	 * Gets the textures width.
	 *
	 * @return The textures width.
	 */
	protected int getWidth() {
		return width;
	}

	/**
	 * Gets the textures height.
	 *
	 * @return The textures height.
	 */
	protected int getHeight() {
		return height;
	}

	/**
	 * Gets if the texture has a alpha channel.
	 *
	 * @return If the texture has a alpha channel.
	 */
	protected boolean hasAlpha() {
		return hasAlpha;
	}
}
