package flounder.textures;

import flounder.factory.*;
import flounder.processing.*;
import flounder.resources.*;

import java.nio.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * Class that represents a loaded texture.
 */
public class TextureObject extends FactoryObject {
	private MyFile file;
	private ByteBuffer buffer;
	private int width;
	private int height;
	private boolean hasAlpha;
	private int numberOfRows;

	private String name;

	private int textureID;
	private int glType;

	/**
	 * A new OpenGL texture object.
	 */
	protected TextureObject() {
		super();
		this.file = null;
		this.hasAlpha = false;
		this.numberOfRows = 1;

		this.name = null;

		this.glType = GL_TEXTURE_2D;
	}

	protected void loadData(MyFile file, ByteBuffer buffer, int width, int height, boolean hasAlpha, int numberOfRows, String name) {
		this.file = file;
		this.buffer = buffer;
		this.width = width;
		this.height = height;
		this.hasAlpha = hasAlpha;
		this.numberOfRows = numberOfRows;

		this.name = name;
	}

	protected void loadGL(int textureID, int glType) {
		this.textureID = textureID;
		this.glType = glType;
	}

	/**
	 * Gets texture file this was stored in.
	 *
	 * @return The texture file.
	 */
	public MyFile getFile() {
		return file;
	}

	/**
	 * Gets the buffer the texture was loaded into.
	 *
	 * @return The texture buffer.
	 */
	public ByteBuffer getBuffer() {
		return buffer;
	}

	/**
	 * Gets the width of the texture.
	 *
	 * @return The textures width.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Gets the height of the texture.
	 *
	 * @return The textures height.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Gets if the texture has alpha.
	 *
	 * @return If the texture has alpha.
	 */
	public boolean hasAlpha() {
		return hasAlpha;
	}

	/**
	 * Gets the number of texture rows.
	 *
	 * @return The number of texture rows.
	 */
	public int getNumberOfRows() {
		return numberOfRows;
	}

	/**
	 * Gets the loaded name for the texture.
	 *
	 * @return The textures name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the textures ID.
	 *
	 * @return The textures ID.
	 */
	public int getTextureID() {
		return textureID;
	}

	/**
	 * The OpenGL type of texture loaded.
	 *
	 * @return The OpenGL texture type.
	 */
	public int getGlType() {
		return glType;
	}

	/**
	 * Deletes the texture from OpenGL memory.
	 */
	public void delete() {
		if (isLoaded()) {
			FlounderProcessors.sendRequest(new TextureDeleteRequest(textureID));
			setFullyLoaded(false);
		}
	}
}
