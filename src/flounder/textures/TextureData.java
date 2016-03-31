package flounder.textures;

import java.nio.*;

public class TextureData {
	private final ByteBuffer m_buffer;
	private final int m_width;
	private final int m_height;

	protected TextureData(final ByteBuffer buffer, final int width, final int height) {
		m_buffer = buffer;
		m_width = width;
		m_height = height;
	}

	protected ByteBuffer getBuffer() {
		return m_buffer;
	}

	protected int getWidth() {
		return m_width;
	}

	protected int getHeight() {
		return m_height;
	}
}
