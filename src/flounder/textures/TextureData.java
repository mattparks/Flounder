package flounder.textures;

import java.nio.*;

public class TextureData {
	private ByteBuffer buffer;
	private int width;
	private int height;

	protected TextureData(ByteBuffer buffer, int width, int height) {
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
