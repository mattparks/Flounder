package flounder.fonts;

import flounder.guis.*;

/**
 * A simple text builder.
 */
public class TextBuilder {
	public static FontType DEFAULT_TYPE = FlounderFonts.COMIC_SANS;

	private String text;
	private float textSize;
	private GuiAlign guiAlign;
	private FontType font;

	/**
	 * Creates a new text builder.
	 *
	 * @param text The inital text.
	 */
	protected TextBuilder(String text) {
		this.text = text;
		this.textSize = 1.0f;
		this.guiAlign = GuiAlign.LEFT;
		this.font = DEFAULT_TYPE;
	}

	/**
	 * Creates the text.
	 *
	 * @return The new text.
	 */
	public Text create() {
		return new Text(text, font, textSize, guiAlign);
	}

	/**
	 * Sets the font size.
	 *
	 * @param size The font size
	 *
	 * @return this.
	 */
	public TextBuilder setFontSize(float size) {
		textSize = size;
		return this;
	}

	/**
	 * Alights the text in a certain way, with relLineWidth in mind.
	 *
	 * @return this.
	 */
	public TextBuilder textAlign(GuiAlign guiAlign) {
		this.guiAlign = guiAlign;
		return this;
	}

	/**
	 * Sets the builder font.
	 *
	 * @param font The font to set as.
	 *
	 * @return this.
	 */
	public TextBuilder setFont(FontType font) {
		this.font = font;
		return this;
	}
}
