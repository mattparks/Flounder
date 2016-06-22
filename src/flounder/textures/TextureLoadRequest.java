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
	private boolean sendRequest;

	/**
	 * Creates a new texture load request.
	 *
	 * @param texture The texture object to load into.
	 * @param builder The builder to load from.
	 * @param sendRequest If a GL request should be sent, if false call {@link #executeGlRequest()} immediacy after {@link #doResourceRequest()}.
	 */
	protected TextureLoadRequest(Texture texture, TextureBuilder builder, boolean sendRequest) {
		this.texture = texture;
		this.builder = builder;
		this.sendRequest = sendRequest;
	}

	@Override
	public void doResourceRequest() {
		data = FlounderEngine.getTextures().decodeTextureFile(builder.getFile());

		if (sendRequest) {
			FlounderEngine.getProcessors().sendGLRequest(this);
		}
	}

	@Override
	public void executeGlRequest() {
		int textureID = FlounderEngine.getTextures().loadTextureToOpenGL(data, builder);
		texture.setTextureID(textureID);
		texture.setFile(builder.getFile());
		texture.setHasTransparency(data.hasAlpha());
	}
}
