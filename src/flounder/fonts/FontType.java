package flounder.fonts;

import flounder.resources.*;

/**
 * Represents a font type that can be used in any text.
 */
public class FontType {
	private TextLoader loader;

	/**
	 * Creates a new font type.
	 *
	 * @param textureAtlas The image that holds the signed distance values.
	 * @param fontFile The file that describes how to renderObjects the font, file usually ends in '.fnt'.
	 */
	public FontType(MyFile textureAtlas, MyFile fontFile) {
		loader = new TextLoader(textureAtlas, fontFile);
	}

	/**
	 * Loads the text.
	 *
	 * @param text The text to load.
	 */
	protected void loadText(Text text) {
		loader.loadTextIntoMemory(text);
	}

	/**
	 * Gets the texture atlas.
	 *
	 * @return The texture atlas.
	 */
	protected int getTextureAtlas() {
		return loader.getFontTextureAtlas();
	}
}
