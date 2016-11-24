package flounder.fonts;

import flounder.framework.*;
import flounder.resources.*;

import java.util.*;

/**
 * Represents a font type that can be used in any text.
 */
public class FontType {
	protected static final List<FontType> ALL_FONT_TYPES = new ArrayList<>();
	protected static final List<FontType> NEEDS_TO_BE_CREATED = new ArrayList<>();

	private MyFile textureAtlas;
	private MyFile fontFile;
	private TextLoader loader;

	/**
	 * Creates a new font type.
	 *
	 * @param textureAtlas The image that holds the signed distance values.
	 * @param fontFile The file that describes how to renderObjects the font, file usually ends in '.fnt'.
	 */
	public FontType(MyFile textureAtlas, MyFile fontFile) {
		this.textureAtlas = textureAtlas;
		this.fontFile = fontFile;

		// If the engine is initialized fonts can be loaded right away, otherwise add to pool.
		if (!FlounderFramework.isInitialized()) {
			NEEDS_TO_BE_CREATED.add(this);
		} else {
			createLoader();
		}

		ALL_FONT_TYPES.add(this);
	}

	/**
	 * Creates the font loader, has to be called after OpenGL is initialized.
	 */
	public void createLoader() {
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
