package flounder.textures;

import flounder.maths.vectors.*;

import java.util.*;

public class TextureTessellatorAtlas {
	private Map<String, Vector2f> texcoords;
	private int width, height;
	private Texture texture;
	private float ax, ay;

	public TextureTessellatorAtlas(final int textureWidth, final int textureHeight, final Texture texture) {
		texcoords = new HashMap<>();
		this.width = textureWidth;
		this.height = textureHeight;
		this.texture = texture;
		ax = 16f / (float) width;
		ay = 16f / (float) height;
	}

	public void registerTextureCoords(final String name, final Vector2f texcoords) {
		this.texcoords.put(name.toLowerCase(), texcoords);
	}

	public Vector2f getTextureCoords(final String name) {
		final Vector2f coords = texcoords.get(name.toLowerCase());

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
