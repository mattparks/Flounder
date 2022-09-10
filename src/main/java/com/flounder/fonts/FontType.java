package com.flounder.fonts;

import com.flounder.resources.*;
import com.flounder.textures.*;

/**
 * Represents a font. It holds the font's texture atlas as well as having the ability to create the quad vertices for any text using this font.
 */
public class FontType {
	private TextLoader loader;

	/**
	 * Creates a new font and loads up the data about each character from the font file.
	 *
	 * @param textureFile The file for the font atlas texture.
	 * @param fontFile The font file containing information about each character in the texture atlas.
	 */
	public FontType(MyFile textureFile, MyFile fontFile) {
		this.loader = new TextLoader(textureFile, fontFile);
	}

	/**
	 * Takes in an unloaded text and calculate all of the vertices for the quads on which this text will be rendered.
	 * The vertex positions and texture coords and calculated based on the information from the font file.
	 * Then takes the information about the vertices of all the quads and stores it in OpenGL.
	 *
	 * @param text The unloaded text.
	 */
	public void loadText(TextObject text) {
		loader.loadTextMesh(text);
	}

	/**
	 * Gets the font texture atlas.
	 *
	 * @return The font texture atlas.
	 */
	public TextureObject getTexture() {
		return loader.getFontTexture();
	}

	/**
	 * Gets the max line height for this font type.
	 *
	 * @return The max line height.
	 */
	public double getMaxSizeY() {
		return loader.getMetaData().getMaxSizeY();
	}
}
