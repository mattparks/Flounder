package flounder.fonts;

public class TextBuilder {
	private final String m_text;
	private boolean m_centered;
	private float m_textSize;
	private FontType m_font;

	protected TextBuilder(final String text) {
		m_text = text;
		m_centered = false;
		m_textSize = 1;
		m_font = FontType.SEGOE_UI;
	}

	public Text create() {
		return new Text(m_text, m_font, m_textSize, m_centered);
	}

	public TextBuilder center() {
		m_centered = true;
		return this;
	}

	public TextBuilder setFontSize(final float size) {
		m_textSize = size;
		return this;
	}

	public TextBuilder setFont(final FontType font) {
		m_font = font;
		return this;
	}
}
