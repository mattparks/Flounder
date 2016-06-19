package flounder.textures;

import flounder.engine.*;
import flounder.processing.*;
import flounder.processing.glProcessing.*;

/**
 * A class that can process a request to load a texture.
 */
public class TextureLoadRequest implements ResourceRequest, GlRequest {
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
	public void doResourceRequest() {
		data = FlounderEngine.getTextures().decodeTextureFile(builder.getFile());
		FlounderEngine.getProcessors().sendGLRequest(this);
	}

	@Override
	public void executeGlRequest() {
		int textureID = FlounderEngine.getTextures().loadTextureToOpenGL(data, builder);
		texture.setTextureID(textureID);
		texture.setFile(builder.getFile());
		texture.setHasTransparency(data.hasAlpha());
	}
}
