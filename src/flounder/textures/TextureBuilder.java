package flounder.textures;

import flounder.logger.*;
import flounder.maths.*;
import flounder.processing.*;
import flounder.resources.*;

import java.lang.ref.*;
import java.util.*;

/**
 * A class capable of setting up a {@link flounder.textures.Texture}.
 */
public class TextureBuilder {
	private static Map<String, SoftReference<Texture>> loadedTextures = new HashMap<>();

	private Colour borderColour;
	private boolean clampEdges;
	private boolean clampToBorder;
	private boolean mipmap;
	private boolean anisotropic;
	private boolean nearest;
	private MyFile file;

	/**
	 * Creates a class to setup a Texture.
	 *
	 * @param textureFile The textures source file.
	 */
	protected TextureBuilder(MyFile textureFile) {
		this.clampEdges = false;
		this.clampToBorder = false;
		this.mipmap = true;
		this.anisotropic = true;
		this.nearest = false;
		this.borderColour = new Colour(0, 0, 0, 0);
		this.file = textureFile;
	}

	/**
	 * Clamps the texture to the edges.
	 *
	 * @return this.
	 */
	public TextureBuilder clampEdges() {
		clampEdges = true;
		clampToBorder = false;
		return this;
	}

	/**
	 * Clamps the texture to a coloured border.
	 *
	 * @param colour The coloured border.
	 *
	 * @return this.
	 */
	public TextureBuilder clampToBorder(Colour colour) {
		clampEdges = false;
		clampToBorder = true;
		borderColour.set(colour);
		return this;
	}

	/**
	 * Selects nearest filtering.
	 *
	 * @return this.
	 */
	public TextureBuilder nearestFiltering() {
		nearest = true;
		return noMipmap();
	}

	/**
	 * Disables mipmapping.
	 *
	 * @return this.
	 */
	public TextureBuilder noMipmap() {
		mipmap = true;
		anisotropic = false;
		return this;
	}

	/**
	 * Disables anisotropic filtering.
	 *
	 * @return this.
	 */
	public TextureBuilder noFiltering() {
		anisotropic = false;
		return this;
	}

	/**
	 * Creates a new texture, carries out the CPU loading, and loads to OpenGL.
	 *
	 * @return The texture that has been created.
	 */
	public Texture create() {
		SoftReference<Texture> ref = loadedTextures.get(file.getPath());
		Texture data = ref == null ? null : ref.get();

		if (data == null) {
			FlounderLogger.log(file.getPath() + " is being loaded into the texture builder right now!");
			loadedTextures.remove(file.getPath());
			data = new Texture();
			FlounderProcessors.sendRequest(new TextureLoadRequest(data, this));
			loadedTextures.put(file.getPath(), new SoftReference<>(data));
		}

		return data;
	}

	/**
	 * Gets if clamping to edges.
	 *
	 * @return If clamping to edges.
	 */
	public boolean isClampEdges() {
		return clampEdges;
	}

	/**
	 * Gets if clamping to border.
	 *
	 * @return If clamping to border.
	 */
	public boolean isClampToBorder() {
		return clampToBorder;
	}

	/**
	 * Gets if mipmapping.
	 *
	 * @return If mipmapping.
	 */
	public boolean isMipmap() {
		return mipmap;
	}

	/**
	 * Gets if anisotropic.
	 *
	 * @return If anisotropic.
	 */
	public boolean isAnisotropic() {
		return anisotropic;
	}

	/**
	 * Gets if nearest filtering.
	 *
	 * @return If nearest filtering.
	 */
	public boolean isNearest() {
		return nearest;
	}

	/**
	 * Gets the border colour.
	 *
	 * @return The border colour.
	 */
	public Colour getBorderColour() {
		return borderColour;
	}

	/**
	 * Gets the source file.
	 *
	 * @return The source file.
	 */
	public MyFile getFile() {
		return file;
	}
}