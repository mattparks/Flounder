package flounder.textures;

import flounder.processing.glProcessing.*;

public class TextureDeleteRequest implements GlRequest {
	private final int m_textureID;

	public TextureDeleteRequest(final int textureID) {
		m_textureID = textureID;
	}

	@Override
	public void executeGlRequest() {
		TextureManager.deleteTexture(m_textureID);
	}
}
