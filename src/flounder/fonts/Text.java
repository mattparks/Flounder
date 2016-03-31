package flounder.fonts;

import flounder.loaders.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.visual.*;

public class Text {
	private final float m_fontSize;
	private final FontType m_fontType;
	private final boolean m_centerText;
	private String m_textString;
	private int m_textMesh;
	private int m_vertexCount;
	private float m_lineMaxSize;
	private int m_numberOfLines;
	private float m_originalWidth;

	private ValueDriver m_positionXDriver;
	private ValueDriver m_positionYDriver;
	private ValueDriver m_alphaDriver;
	private ValueDriver m_scaleDriver;
	private ValueDriver m_glowDriver;
	private ValueDriver m_borderDriver;
	private Colour m_colour;
	private Colour m_borderColour;

	private boolean m_solidBorder;
	private boolean m_glowBorder;

	private float m_currentScale;
	private float m_currentX;
	private float m_currentY;
	private float m_currentAlpha;
	private float m_glowSize;
	private float m_borderSize;

	private boolean m_loaded;

	protected Text(final String text, final FontType font, final float fontSize, final boolean centered) {
		m_textString = text;
		m_fontSize = fontSize;
		m_fontType = font;
		m_centerText = centered;

		m_alphaDriver = new ConstantDriver(1);
		m_scaleDriver = new ConstantDriver(1);
		m_glowDriver = new ConstantDriver(0);
		m_borderDriver = new ConstantDriver(0);
		m_colour = new Colour(0f, 0f, 0f);
		m_borderColour = new Colour(1f, 1f, 1f);

		m_solidBorder = false;
		m_glowBorder = false;

		m_glowSize = 0;
		m_borderSize = 0;

		m_loaded = false;
	}

	public static TextBuilder newText(final String text) {
		return new TextBuilder(text);
	}

	public void initialise(final float absX, final float absY, final float maxXLength) {
		m_positionXDriver = new ConstantDriver(absX);
		m_positionYDriver = new ConstantDriver(absY);
		m_lineMaxSize = maxXLength;

		if (!m_loaded) {
			m_fontType.loadText(this);
			m_loaded = true;
		}
	}

	public void update(final float delta) {
		m_currentScale = m_scaleDriver.update(delta);
		m_currentX = m_positionXDriver.update(delta);
		m_currentY = m_positionYDriver.update(delta);
		m_currentAlpha = m_alphaDriver.update(delta);
		m_glowSize = m_glowDriver.update(delta);
		m_borderSize = m_borderDriver.update(delta);
	}

	protected Vector2f getPosition() {
		float scaleFactor = (m_currentScale - 1f) / 2f;
		float xChange = scaleFactor * m_originalWidth;
		float yChange = scaleFactor * (float) TextLoader.LINE_HEIGHT * m_fontSize * m_numberOfLines * 1f;
		return new Vector2f(m_currentX - xChange, m_currentY - yChange);
	}

	protected float getTotalBorderSize() {
		if (m_solidBorder) {
			if (m_borderSize == 0) {
				return 0;
			} else {
				return calculateEdgeStart() + m_borderSize;
			}
		} else if (m_glowBorder) {
			return calculateEdgeStart();
		} else {
			return 0;
		}
	}

	protected float calculateEdgeStart() {
		float size = m_fontSize * m_currentScale;
		return 1f / 300f * size + 137f / 300f;
	}

	public String getTextString() {
		return m_textString;
	}

	public void setText(String newText) {
		deleteFromMemory();
		m_textString = newText;
		m_fontType.loadText(this);
	}

	public void deleteFromMemory() {
		Loader.deleteVAOFromCache(m_textMesh);
	}

	public float getFontSize() {
		return m_fontSize;
	}

	public FontType getFontType() {
		return m_fontType;
	}

	public boolean isCentered() {
		return m_centerText;
	}

	protected int getMesh() {
		return m_textMesh;
	}

	protected void setMeshInfo(final int vao, final int verticesCount) {
		m_textMesh = vao;
		m_vertexCount = verticesCount;
	}

	protected int getVertexCount() {
		return m_vertexCount;
	}

	protected float getMaxLineSize() {
		return m_lineMaxSize;
	}

	protected void setOriginalWidth(final float width) {
		m_originalWidth = width;
	}

	public Colour getColour() {
		return m_colour;
	}

	public void setColour(Colour colour) {
		m_colour = colour;
	}

	protected Colour getBorderColour() {
		return m_borderColour;
	}

	public void setBorderColour(final float r, final float g, final float b) {
		m_borderColour.set(r, g, b);
	}

	public void setScaleDriver(final ValueDriver scaleDriver) {
		m_scaleDriver = scaleDriver;
	}

	public void setBorder(final ValueDriver driver) {
		m_borderDriver = driver;
		m_solidBorder = true;
		m_glowBorder = false;
	}

	public void removeBorder() {
		m_solidBorder = false;
		m_glowBorder = false;
	}

	public void setGlowing(final ValueDriver driver) {
		m_solidBorder = false;
		m_glowBorder = true;
		m_glowDriver = driver;
	}

	public void setAlphaDriver(final ValueDriver driver) {
		m_alphaDriver = driver;
	}

	public float getScale() {
		return m_currentScale;
	}

	public int getNumberOfLines() {
		return m_numberOfLines;
	}

	public void setNumberOfLines(final int number) {
		m_numberOfLines = number;
	}

	public float getBorderSize() {
		return m_borderSize;
	}

	protected float getGlowSize() {
		if (m_solidBorder) {
			return calculateAntialiasSize();
		} else if (m_glowBorder) {
			return m_glowSize;
		} else {
			return 0;
		}
	}

	protected float calculateAntialiasSize() {
		float size = m_fontSize * m_currentScale;
		size = (size - 1) / (1f + size / 4f) + 1f;
		return 0.1f / size;
	}

	protected float getCurrentWidth() {
		return m_originalWidth * m_currentScale;
	}

	protected float getTransparency() {
		return m_currentAlpha;
	}
}
