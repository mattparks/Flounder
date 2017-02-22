package flounder.textures;

import flounder.processing.opengl.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * A class that can process a request to delete a texture.
 */
public class TextureDeleteRequest implements RequestOpenGL {
	private TextureObject texture;

	/**
	 * Creates a new texture delete request.
	 *
	 * @param texture The OpenGL texture to be deleted.
	 */
	public TextureDeleteRequest(TextureObject texture) {
		this.texture = texture;
	}

	@Override
	public void executeRequestGL() {
		glDeleteTextures(texture.getTextureID());
		FlounderTextures.getLoaded().get(texture.getName());
	}
}
