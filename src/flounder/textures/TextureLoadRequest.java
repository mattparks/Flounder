package flounder.textures;

import flounder.processing.opengl.*;
import flounder.processing.resource.*;

/**
 * A class that can process a request to load a texture.
 */
public class TextureLoadRequest implements RequestResource, RequestOpenGL {
	private Texture texture;
	private TextureBuilder builder;
	private TextureData data;

	/**
	 * Creates a new texture load request.
	 *
	 * @param texture The texture object to load into.
	 * @param builder The builder to load from.
	 */
	protected TextureLoadRequest(Texture texture, TextureBuilder builder) {
		this.texture = texture;
		this.builder = builder;
	}

	@Override
	public void executeRequestResource() {
		data = FlounderTextures.decodeTextureFile(builder.getFile());
	}

	@Override
	public void executeRequestGL() {
		while (data == null) {
			// Wait for resources to load into data...
		}

		int textureID = FlounderTextures.loadTextureToOpenGL(data, builder);
		texture.setTextureID(textureID);
		texture.setFile(builder.getFile());
		texture.setHasTransparency(data.hasAlpha());
	}
}