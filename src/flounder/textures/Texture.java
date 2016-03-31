package flounder.textures;

import flounder.processing.glProcessing.*;
import flounder.resources.*;

/**
 * Class that represents a loaded texture.
 */
public class Texture {
	private int textureID;
	private boolean hasTransparency;
	private int numberOfRows;
	private MyFile file;
	private boolean loaded;

	protected Texture() {
		hasTransparency = false;
		numberOfRows = 1;
		loaded = false;
	}

	/**
	 * Creates a new Texture Builder.
	 *
	 * @param file The texture file to be loaded.
	 *
	 * @return A new Texture Builder.
	 */
	public static TextureBuilder newTexture(final MyFile file) {
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

	public int getTextureID() {
		return textureID;
	}

	public void setTextureID(final int id) {
		textureID = id;
		loaded = true;
	}

	public boolean hasTransparency() {
		return hasTransparency;
	}

	public void setHasTransparency(final boolean hasTransparency) {
		this.hasTransparency = hasTransparency;
	}

	public int getNumberOfRows() {
		return numberOfRows;
	}

	public void setNumberOfRows(final int numberOfRows) {
		this.numberOfRows = numberOfRows;
	}

	public MyFile getFile() {
		return file;
	}

	public void setFile(final MyFile file) {
		this.file = file;
	}

	public boolean isLoaded() {
		return loaded;
	}

	/**
	 * Sends a request to delete the texture.
	 */
	public void delete() {
		loaded = false;
		GlRequestProcessor.sendRequest(new TextureDeleteRequest(textureID));
	}
}
