package flounder.textures;

import flounder.maths.vectors.*;

import java.util.*;

/**
 * A class that can create a tessellator around a texture.
 */
public class TextureTessellatorAtlas {
	private Map<String, Vector2f> textureCoords;
	private int width;
	private int height;
	private Texture texture;
	private float ax, ay;

	/**
	 * Creates a new tessellator atlas.
	 *
	 * @param textureWidth The textures width.
	 * @param textureHeight The textures height.
	 * @param texture The texture.
	 */
	public TextureTessellatorAtlas(int textureWidth, int textureHeight, Texture texture) {
		textureCoords = new HashMap<>();
		this.width = textureWidth;
		this.height = textureHeight;
		this.texture = texture;
		ax = 16.0f / (float) width;
		ay = 16.0f / (float) height;
	}

	/**
	 * Registers a name to a set of texture coords.
	 *
	 * @param name The name to register.
	 * @param texcoords The coords to register.
	 */
	public void registerTextureCoords(String name, Vector2f texcoords) {
		this.textureCoords.put(name.toLowerCase(), texcoords);
	}

	/**
	 * Gets a texture coords from a name.
	 *
	 * @param name The registered name to get coords from.
	 *
	 * @return The coords found.
	 */
	public Vector2f getTextureCoords(String name) {
		Vector2f coords = textureCoords.get(name.toLowerCase());

		if (coords == null) {
			return new Vector2f(0, 0);
		}

		// TODO: Do some maths off of this..

		return coords;
	}

	/**
	 * Gets this tessellators texture atlas.
	 *
	 * @return The texture atlas.
	 */
	public Texture getTexture() {
		return texture;
	}
}
