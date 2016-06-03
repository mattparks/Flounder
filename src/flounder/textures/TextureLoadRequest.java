package flounder.textures;

import flounder.processing.*;
import flounder.processing.glProcessing.*;

public class TextureLoadRequest implements ResourceRequest, GlRequest {
	private Texture texture;
	private TextureBuilder builder;
	private TextureData data;

	protected TextureLoadRequest(Texture texture, TextureBuilder builder) {
		this.texture = texture;
		this.builder = builder;
	}

	@Override
	public void doResourceRequest() {
		data = TextureManager.decodeTextureFile(builder.getFile());
		GlRequestProcessor.sendRequest(this);
	}

	@Override
	public void executeGlRequest() {
		int texID = TextureManager.loadTextureToOpenGL(data, builder);
		texture.setTextureID(texID);
		texture.setFile(builder.getFile());
	}
}
