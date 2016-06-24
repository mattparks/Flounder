package flounder.fonts;

import flounder.engine.*;

public class TextBuilder {
	public static FontType DEFAULT_TYPE = FlounderEngine.getFonts().comicSans;

	private String text;
	private boolean centred;
	private float textSize;
	private FontType font;

	protected TextBuilder(String text) {
		this.text = text;
		this.centred = false;
		this.textSize = 1.0f;
		this.font = DEFAULT_TYPE;
	}

	public Text create() {
		return new Text(text, font, textSize, centred);
	}

	public TextBuilder centre() {
		centred = true;
		return this;
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
