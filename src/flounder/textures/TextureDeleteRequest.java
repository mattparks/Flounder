package flounder.textures;

import flounder.processing.glProcessing.*;

public class TextureDeleteRequest implements GlRequest {
	private int textureID;

	public TextureDeleteRequest(int textureID) {
		this.textureID = textureID;
	}

	@Override
	public void executeGlRequest() {
		TextureManager.deleteTexture(textureID);
	}
}
