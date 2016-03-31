package flounder.fonts;

import flounder.resources.*;

/**
 * Represents a font type that can be used in any text.
 */
public class FontType {
	public static final MyFile FONTS_LOC = new MyFile(MyFile.RES_FOLDER, "fonts");
	public static final FontType SEGOE_UI = new FontType(new MyFile(FONTS_LOC, "segoeUI.png"), new MyFile(FONTS_LOC, "segoeUI.fnt"));

	private final TextLoader loader;

	/**
	 * Creates a new font type.
	 *
	 * @param textureAtlas The image that holds the signed distance values.
	 * @param fontFile The file that describes how to renderObjects the font, file usually ends in '.fnt'.
	 */
	public FontType(final MyFile textureAtlas, final MyFile fontFile) {
		loader = new TextLoader(textureAtlas, fontFile);
	}

	protected void loadText(Text text) {
		loader.loadTextIntoMemory(text);
	}

	protected int getTextureAtlas() {
		return loader.getFontTextureAtlas();
	}
}
