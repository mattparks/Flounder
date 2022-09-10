package com.flounder.textures;

import com.flounder.processing.opengl.*;

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
		if (!FlounderTextures.get().getLoaded().containsKey(texture.getName())) {
			return;
		}

		FlounderTextures.get().getLoaded().get(texture.getName()).clear();
		FlounderTextures.get().getLoaded().remove(texture.getName());
		FlounderTextures.get().deleteTexture(texture.getTextureID());
	}
}
