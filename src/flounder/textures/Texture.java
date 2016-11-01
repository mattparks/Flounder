package flounder.textures;

import flounder.processing.*;
import flounder.resources.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

/**
 * Class that represents a loaded texture.
 */
public class Texture {
	private int textureID;
	private int glType;
	private boolean hasTransparency;
	private int numberOfRows;
	private MyFile file;
	private boolean loaded;

	/**
	 * A new OpenGL FBO object.
	 */
	protected Texture() {
		this.glType = GL_TEXTURE_2D;
		this.hasTransparency = false;
		this.numberOfRows = 1;
		this.loaded = false;
	}

	/**
	 * Creates a new Texture Builder.
	 *
	 * @param file The texture file to be loaded.
	 *
	 * @return A new Texture Builder.
	 */
	public static TextureBuilder newTexture(MyFile file) {
		return new TextureBuilder(file);
	}

	/**
	 * Creates a new empty Texture.
	 *
	 * @return A new empty Texture.
	 */
	public static Texture getEmptyTexture() {
		return new Texture();
	}

	public static Texture newCubeMap(MyFile[] textureFiles) {
		Texture texture = new Texture();
		texture.textureID = FlounderTextures.loadCubeMap(textureFiles);
		texture.glType = GL_TEXTURE_CUBE_MAP;
		return texture;
	}

	public static Texture newEmptyCubeMap(int size) {
		Texture texture = new Texture();
		texture.textureID = FlounderTextures.createEmptyCubeMap(size);
		texture.glType = GL_TEXTURE_CUBE_MAP;
		return texture;
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
	 * Sets the texture ID (loads the texture).
	 *
	 * @param id The textures ID.
	 */
	public void setTextureID(int id) {
		textureID = id;
		loaded = true;
	}

	/**
	 * Gets if the texture has transparency.
	 *
	 * @return If the texture has transparency.
	 */
	public boolean hasTransparency() {
		return hasTransparency;
	}

	/**
	 * Sets if the texture has transparency (should be already set from the loader).
	 *
	 * @param hasTransparency If the texture has transparency.
	 */
	public void setHasTransparency(boolean hasTransparency) {
		this.hasTransparency = hasTransparency;
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
	 * Sets the number of rows in the texture.
	 *
	 * @param numberOfRows The number of rows in the texture.
	 */
	public void setNumberOfRows(int numberOfRows) {
		this.numberOfRows = numberOfRows;
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
	 * Sets the file this texture was loaded from.
	 *
	 * @param file The file this texture was loaded from.
	 */
	public void setFile(MyFile file) {
		this.file = file;
	}

	/**
	 * Gets if the texture is loaded.
	 *
	 * @return If the texture is loaded.
	 */
	public boolean isLoaded() {
		return loaded;
	}

	/**
	 * Sends a request to delete the texture.
	 */
	public void delete() {
		loaded = false;
		FlounderProcessors.sendGLRequest(new TextureDeleteRequest(textureID));
	}
}
