package flounder.fonts;

import flounder.resources.*;
import flounder.textures.*;

/**
 * Represents a font. It holds the font's texture atlas as well as having the ability to create the quad vertices for any text using this font.
 */
public class FontType {
	private TextLoader loader;

	/**
	 * Creates a new font and loads up the data about each character from the font file.
	 *
	 * @param fontSheet The file for the font atlas texture.
	 * @param fontFile The font file containing information about each character in the texture atlas.
	 */
	public FontType(MyFile fontSheet, MyFile fontFile) {
		this.loader = new TextLoader(fontSheet, fontFile);
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
	 * Takes in an unloaded text and calculate all of the vertices for the quads on which this text will be rendered.
	 * The vertex positions and texture coords and calculated based on the information from the font file.
	 *
	 * @param text The unloaded text.
	 *
	 * @return Information about the vertices of all the quads.
	 */
	public TextMeshData loadText(Text text) {
		return loader.createTextMesh(text);
	}
}
