package flounder.fonts;

public class TextBuilder {
	public static FontType DEFAULT_TYPE = FontManager.COMIC_SANS;

	private final String text;
	private boolean centered;
	private float textSize;
	private FontType font;

	protected TextBuilder(final String text) {
		this.text = text;
		this.centered = false;
		this.textSize = 1.0f;
		this.font = DEFAULT_TYPE;
	}

	public Text create() {
		return new Text(text, font, textSize, centered);
	}

	public TextBuilder center() {
		centered = true;
		return this;
	}

	public TextBuilder setFontSize(final float size) {
		textSize = size;
		return this;
	}

	public TextBuilder setFont(final FontType font) {
		this.font = font;
		return this;
	}
}
