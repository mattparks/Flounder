package flounder.textures;

import flounder.processing.*;
import flounder.processing.glProcessing.*;

public class TextureLoadRequest implements ResourceRequest, GlRequest {
	private final Texture m_texture;
	private final TextureBuilder m_builder;
	private TextureData m_data;

	protected TextureLoadRequest(final Texture texture, final TextureBuilder builder) {
		m_texture = texture;
		m_builder = builder;
	}

	@Override
	public void doResourceRequest() {
		m_data = TextureManager.decodeTextureFile(m_builder.getFile());
		GlRequestProcessor.sendRequest(this);
	}

	@Override
	public void executeGlRequest() {
		int texID = TextureManager.loadTextureToOpenGL(m_data, m_builder);
		m_texture.setTextureID(texID);
		m_texture.setFile(m_builder.getFile());
	}
}
