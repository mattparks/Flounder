package flounder.textures;

import flounder.processing.opengl.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * A class that can process a request to delete a texture.
 */
public class TextureDeleteRequest implements RequestOpenGL {
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
	public void executeRequestGL() {
		glDeleteTextures(textureID);
	}
}
