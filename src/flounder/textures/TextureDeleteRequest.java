package flounder.textures;

import flounder.processing.glProcessing.*;

public class TextureDeleteRequest implements GlRequest {
	private final int textureID;

	public TextureDeleteRequest(final int textureID) {
		this.textureID = textureID;
	}

	@Override
	public void executeGlRequest() {
		TextureManager.deleteTexture(textureID);
	}
}
