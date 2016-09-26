package flounder.textures;

import flounder.processing.glProcessing.*;

/**
 * A class that can process a request to delete a texture.
 */
public class TextureDeleteRequest implements GlRequest {
	private int textureID;

	/**
	 * Creates a new texture delete request.
	 *
	 * @param textureID The OpenGL texture ID to be deleted.
	 */
	public TextureDeleteRequest(int textureID) {
		this.textureID = textureID;
	}

	@Override
	public void executeGlRequest() {
		FlounderTextures.deleteTexture(textureID);
	}
}
