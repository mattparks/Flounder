package flounder.textures;

import flounder.processing.glProcessing.*;
import flounder.resources.*;

/**
 * Class that represents a loaded texture.
 */
public class Texture {
	private int m_textureID;
	private boolean m_hasTransparency;
	private int m_numberOfRows;
	private MyFile m_file;
	private boolean m_loaded;

	protected Texture() {
		m_hasTransparency = false;
		m_numberOfRows = 1;
		m_loaded = false;
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
		return m_textureID;
	}

	public void setTextureID(final int id) {
		m_textureID = id;
		m_loaded = true;
	}

	public boolean hasTransparency() {
		return m_hasTransparency;
	}

	public void setHasTransparency(final boolean hasTransparency) {
		m_hasTransparency = hasTransparency;
	}

	public int getNumberOfRows() {
		return m_numberOfRows;
	}

	public void setNumberOfRows(final int numberOfRows) {
		m_numberOfRows = numberOfRows;
	}

	public MyFile getFile() {
		return m_file;
	}

	public void setFile(final MyFile file) {
		m_file = file;
	}

	public boolean isLoaded() {
		return m_loaded;
	}

	/**
	 * Sends a request to delete the texture.
	 */
	public void delete() {
		m_loaded = false;
		GlRequestProcessor.sendRequest(new TextureDeleteRequest(m_textureID));
	}
}
