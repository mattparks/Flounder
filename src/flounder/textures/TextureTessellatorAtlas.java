package flounder.textures;

import flounder.maths.vectors.*;

import java.util.*;

public class TextureTessellatorAtlas {
	private Map<String, Vector2f> textureCoords;
	private int width;
	private int height;
	private Texture texture;
	private float ax, ay;

	public TextureTessellatorAtlas(int textureWidth, int textureHeight, Texture texture) {
		textureCoords = new HashMap<>();
		this.width = textureWidth;
		this.height = textureHeight;
		this.texture = texture;
		ax = 16f / (float) width;
		ay = 16f / (float) height;
	}

	public void registerTextureCoords(String name, Vector2f texcoords) {
		this.textureCoords.put(name.toLowerCase(), texcoords);
	}

	public Vector2f getTextureCoords(String name) {
		Vector2f coords = textureCoords.get(name.toLowerCase());

		if (coords == null) {
			return new Vector2f(0, 0);
		}

		// TODO: Do some maths off of this..

		return coords;
	}

	public Texture getTexture() {
		return texture;
	}
}
