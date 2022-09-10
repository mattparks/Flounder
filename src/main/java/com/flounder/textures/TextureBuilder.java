package com.flounder.textures;

import com.flounder.factory.*;
import com.flounder.maths.*;
import com.flounder.resources.*;

/**
 * A builder used to set texture parameters for loading.
 */
public class TextureBuilder extends FactoryBuilder {
	private MyFile file;
	private MyFile[] cubemap;

	private Colour borderColour;
	private boolean clampToBorder;
	private boolean clampEdges;
	private boolean mipmap;
	private boolean anisotropic;
	private boolean nearest;
	private int numberOfRows;

	protected TextureBuilder(Factory factory) {
		super(factory);
		this.file = null;
		this.cubemap = null;

		this.borderColour = new Colour();
		this.clampToBorder = false;
		this.clampEdges = false;
		this.mipmap = true;
		this.anisotropic = true;
		this.nearest = false;
		this.numberOfRows = 1;
	}

	/**
	 * Sets the textures source file.
	 *
	 * @param file The source file.
	 *
	 * @return this.
	 */
	public TextureBuilder setFile(MyFile file) {
		this.file = file;
		return this;
	}

	/**
	 * Sets the cubemap source files.
	 *
	 * @param cubemap The source cubemap files.
	 *
	 * @return this.
	 */
	public TextureBuilder setCubemap(MyFile[] cubemap) {
		this.cubemap = cubemap;
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
		this.borderColour.set(colour);
		this.clampToBorder = true;
		this.clampEdges = false;
		return this;
	}

	/**
	 * Clamps the texture to the edges.
	 *
	 * @return this.
	 */
	public TextureBuilder clampEdges() {
		this.clampToBorder = false;
		this.clampEdges = true;
		return this;
	}

	/**
	 * Selects nearest filtering.
	 *
	 * @return this.
	 */
	public TextureBuilder nearestFiltering() {
		this.nearest = true;
		return noMipmap();
	}

	/**
	 * Disables mipmapping.
	 *
	 * @return this.
	 */
	public TextureBuilder noMipmap() {
		this.mipmap = true;
		this.anisotropic = false;
		return this;
	}

	/**
	 * Disables anisotropic filtering.
	 *
	 * @return this.
	 */
	public TextureBuilder noFiltering() {
		this.anisotropic = false;
		return this;
	}

	/**
	 * Sets the starting number of texture rows (default = 1).
	 *
	 * @param numberOfRows The new number of rows.
	 *
	 * @return this.
	 */
	public TextureBuilder setNumberOfRows(int numberOfRows) {
		this.numberOfRows = numberOfRows;
		return this;
	}

	/**
	 * Gets the source file.
	 *
	 * @return The source file.
	 */
	public MyFile getFile() {
		return file;
	}

	/**
	 * Gets the cubemap source files.
	 *
	 * @return The cubemap source files.
	 */
	public MyFile[] getCubemap() {
		return cubemap;
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
	 * Gets if clamping to border.
	 *
	 * @return If clamping to border.
	 */
	public boolean isClampToBorder() {
		return clampToBorder;
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
	 * Gets the number of rows.
	 *
	 * @return The number of rows.
	 */
	public int getNumberOfRows() {
		return numberOfRows;
	}

	@Override
	public TextureObject create() {
		if (file != null) {
			return (TextureObject) builderCreate(file.getPath());
		} else if (cubemap != null && cubemap.length >= 1) {
			return (TextureObject) builderCreate(cubemap[0].getPath());
		}

		return null;
	}

	@Override
	public String toString() {
		return "TextureBuilder{" +
				"file=" + file +
				", cubemap=" + cubemap +
				", borderColour=" + borderColour +
				", clampToBorder=" + clampToBorder +
				", clampEdges=" + clampEdges +
				", mipmap=" + mipmap +
				", anisotropic=" + anisotropic +
				", nearest=" + nearest +
				", numberOfRows=" + numberOfRows +
				'}';
	}
}
