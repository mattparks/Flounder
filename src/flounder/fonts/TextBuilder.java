package flounder.fonts;

import flounder.engine.*;

public class TextBuilder {
	public static FontType DEFAULT_TYPE = FlounderEngine.getFonts().comicSans;

	private String text;
	private TextAlign textAlign;
	private float textSize;
	private FontType font;

	protected TextBuilder(String text, TextAlign textAlign) {
		this.text = text;
		this.textAlign = textAlign;
		this.textSize = 1.0f;
		this.font = DEFAULT_TYPE;
	}

	public Text create() {
		return new Text(text, font, textSize, textAlign);
	}

	public TextBuilder setFontSize(float size) {
		textSize = size;
		return this;
	}

	public TextBuilder setFont(FontType font) {
		this.font = font;
		return this;
	}
}
