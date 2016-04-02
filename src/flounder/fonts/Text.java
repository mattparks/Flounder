package flounder.fonts;

import flounder.loaders.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.visual.*;

public class Text {
	private final float fontSize;
	private final FontType fontType;
	private final boolean centerText;
	private String textString;
	private int textMesh;
	private int vertexCount;
	private float lineMaxSize;
	private int numberOfLines;
	private float originalWidth;

	private ValueDriver positionXDriver;
	private ValueDriver positionYDriver;
	private ValueDriver alphaDriver;
	private ValueDriver scaleDriver;
	private ValueDriver glowDriver;
	private ValueDriver borderDriver;
	private Colour colour;
	private Colour borderColour;

	private boolean solidBorder;
	private boolean glowBorder;

	private float currentScale;
	private float currentX;
	private float currentY;
	private float currentAlpha;
	private float glowSize;
	private float borderSize;

	private boolean loaded;

	protected Text(final String text, final FontType font, final float fontSize, final boolean centered) {
		textString = text;
		this.fontSize = fontSize;
		fontType = font;
		centerText = centered;

		alphaDriver = new ConstantDriver(1.0f);
		scaleDriver = new ConstantDriver(1.0f);
		glowDriver = new ConstantDriver(0.0f);
		borderDriver = new ConstantDriver(0.0f);
		colour = new Colour(0.0f, 0.0f, 0.0f, 1.0f);
		borderColour = new Colour(1.0f, 1.0f, 1.0f, 1.0f);

		solidBorder = false;
		glowBorder = false;

		glowSize = 0.0f;
		borderSize = 0.0f;

		loaded = false;
	}

	public static TextBuilder newText(final String text) {
		return new TextBuilder(text);
	}

	public void initialize(final float absX, final float absY, final float maxXLength) {
		positionXDriver = new ConstantDriver(absX);
		positionYDriver = new ConstantDriver(absY);
		lineMaxSize = maxXLength;

		if (!loaded) {
			fontType.loadText(this);
			loaded = true;
		}
	}

	public void update(final float delta) {
		currentScale = scaleDriver.update(delta);
		currentX = positionXDriver.update(delta);
		currentY = positionYDriver.update(delta);
		currentAlpha = alphaDriver.update(delta);
		glowSize = glowDriver.update(delta);
		borderSize = borderDriver.update(delta);
	}

	protected Vector2f getPosition() {
		float scaleFactor = (currentScale - 1.0f) / 2.0f;
		float xChange = scaleFactor * originalWidth;
		float yChange = scaleFactor * (float) TextLoader.LINE_HEIGHT * fontSize * numberOfLines * 1.0f;
		return new Vector2f(currentX - xChange, currentY - yChange);
	}

	protected float getTotalBorderSize() {
		if (solidBorder) {
			if (borderSize == 0.0f) {
				return 0.0f;
			} else {
				return calculateEdgeStart() + borderSize;
			}
		} else if (glowBorder) {
			return calculateEdgeStart();
		} else {
			return 0.0f;
		}
	}

	protected float calculateEdgeStart() {
		float size = fontSize * currentScale;
		return 1.0f / 300.0f * size + 137.0f / 300.0f;
	}

	public String getTextString() {
		return textString;
	}

	public void setText(String newText) {
		deleteFromMemory();
		textString = newText;
		fontType.loadText(this);
	}

	public void deleteFromMemory() {
		Loader.deleteVAOFromCache(textMesh);
	}

	public float getFontSize() {
		return fontSize;
	}

	public FontType getFontType() {
		return fontType;
	}

	public boolean isCentered() {
		return centerText;
	}

	protected int getMesh() {
		return textMesh;
	}

	protected void setMeshInfo(final int vao, final int verticesCount) {
		textMesh = vao;
		vertexCount = verticesCount;
	}

	protected int getVertexCount() {
		return vertexCount;
	}

	protected float getMaxLineSize() {
		return lineMaxSize;
	}

	protected void setOriginalWidth(final float width) {
		originalWidth = width;
	}

	public Colour getColour() {
		return colour;
	}

	public void setColour(Colour colour) {
		this.colour = colour;
	}

	protected Colour getBorderColour() {
		return borderColour;
	}

	public void setBorderColour(final float r, final float g, final float b) {
		borderColour.set(r, g, b);
	}

	public void setScaleDriver(final ValueDriver scaleDriver) {
		this.scaleDriver = scaleDriver;
	}

	public void setBorder(final ValueDriver driver) {
		borderDriver = driver;
		solidBorder = true;
		glowBorder = false;
	}

	public void removeBorder() {
		solidBorder = false;
		glowBorder = false;
	}

	public void setGlowing(final ValueDriver driver) {
		solidBorder = false;
		glowBorder = true;
		glowDriver = driver;
	}

	public void setAlphaDriver(final ValueDriver driver) {
		alphaDriver = driver;
	}

	public float getScale() {
		return currentScale;
	}

	public int getNumberOfLines() {
		return numberOfLines;
	}

	public void setNumberOfLines(final int number) {
		numberOfLines = number;
	}

	public float getBorderSize() {
		return borderSize;
	}

	protected float getGlowSize() {
		if (solidBorder) {
			return calculateAntialiasSize();
		} else if (glowBorder) {
			return glowSize;
		} else {
			return 0;
		}
	}

	protected float calculateAntialiasSize() {
		float size = fontSize * currentScale;
		size = (size - 1.0f) / (1.0f + size / 4.0f) + 1.0f;
		return 0.1f / size;
	}

	protected float getCurrentWidth() {
		return originalWidth * currentScale;
	}

	protected float getTransparency() {
		return currentAlpha;
	}
}
