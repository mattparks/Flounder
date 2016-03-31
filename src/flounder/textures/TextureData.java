package flounder.textures;

import java.nio.*;

public class TextureData {
	private final ByteBuffer buffer;
	private final int width;
	private final int height;

	protected TextureData(final ByteBuffer buffer, final int width, final int height) {
		this.buffer = buffer;
		this.width = width;
		this.height = height;
	}

	protected ByteBuffer getBuffer() {
		return buffer;
	}

	protected int getWidth() {
		return width;
	}

	protected int getHeight() {
		return height;
	}
}
