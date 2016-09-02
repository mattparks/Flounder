package flounder.fonts;

import flounder.engine.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.visual.*;

public class Text {
	private float fontSize;
	private FontType fontType;
	private TextAlign textAlign;
	private String textString;
	private int textMesh;
	private int vertexCount;
	private float lineMaxSize;
	private int numberOfLines;
	private float originalWidth;
	private float originalHeight;

	private String newText;

	private ValueDriver positionXDriver;
	private ValueDriver positionYDriver;
	private ValueDriver alphaDriver;
	private ValueDriver scaleDriver;
	private ValueDriver glowDriver;
	private ValueDriver borderDriver;
	private Colour colour;
	private Colour borderColour;
	private Vector2f position;

	private boolean solidBorder;
	private boolean glowBorder;

	private float currentScale;
	private float currentX;
	private float currentY;
	private float currentAlpha;
	private float glowSize;
	private float borderSize;

	private boolean loaded;

	protected Text(String text, FontType font, float fontSize, TextAlign textAlign) {
		this.textString = text;
		this.fontSize = fontSize;
		this.fontType = font;
		this.textAlign = textAlign;

		this.alphaDriver = new ConstantDriver(1.0f);
		this.scaleDriver = new ConstantDriver(1.0f);
		this.glowDriver = new ConstantDriver(0.0f);
		this.borderDriver = new ConstantDriver(0.0f);
		this.colour = new Colour(0.0f, 0.0f, 0.0f, 1.0f);
		this.borderColour = new Colour(1.0f, 1.0f, 1.0f, 1.0f);
		this.position = new Vector2f();

		this.solidBorder = false;
		this.glowBorder = false;

		this.glowSize = 0.0f;
		this.borderSize = 0.0f;

		this.loaded = false;
	}

	public static TextBuilder newText(String text, TextAlign textAlign) {
		return new TextBuilder(text, textAlign);
	}

	public void init(float absX, float absY, float maxXLength) {
		positionXDriver = new ConstantDriver(absX);
		positionYDriver = new ConstantDriver(absY);
		lineMaxSize = maxXLength;

		if (!loaded) {
			fontType.loadText(this);
			loaded = true;
		}
	}

	public void update(float delta) {
		if (loaded && newText != null) {
			deleteFromMemory();
			textString = newText;
			fontType.loadText(this);
			newText = null;
		}

		currentScale = scaleDriver.update(delta);
		currentX = positionXDriver.update(delta);
		currentY = positionYDriver.update(delta);
		currentAlpha = alphaDriver.update(delta);
		glowSize = glowDriver.update(delta);
		borderSize = borderDriver.update(delta);

		switch (textAlign) {
			case LEFT:
				break;
			case CENTRE:
				break;
			case RIGHT:
				break;
		}
	}

	protected Vector2f getPosition() {
		float scaleFactor = (currentScale - 1.0f) / 2.0f;
		float xChange = scaleFactor * originalWidth;
		float yChange = scaleFactor * originalHeight;
		return position.set(currentX - xChange, currentY - yChange);
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
		if (!textString.equals(newText)) {
			this.newText = newText;
		}
	}

	public void deleteFromMemory() {
		FlounderEngine.getLoader().deleteVAOFromCache(textMesh);
	}

	public float getFontSize() {
		return fontSize;
	}

	public FontType getFontType() {
		return fontType;
	}

	public TextAlign getTextAlign() {
		return textAlign;
	}

	public int getMesh() {
		return textMesh;
	}

	protected void setMeshInfo(int vao, int verticesCount) {
		textMesh = vao;
		vertexCount = verticesCount;
	}

	public int getVertexCount() {
		return vertexCount;
	}

	public float getMaxLineSize() {
		return lineMaxSize;
	}

	protected void setOriginalWidth(float width) {
		originalWidth = width;
	}

	protected void setOriginalHeight(float height) {
		originalHeight = height;
	}

	public Colour getColour() {
		return colour;
	}

	public void setColour(Colour colour) {
		this.colour.set(colour);
	}

	public void setColour(float r, float g, float b) {
		this.colour.set(r, g, b);
	}

	public Colour getBorderColour() {
		return borderColour;
	}

	public void setBorderColour(float r, float g, float b) {
		borderColour.set(r, g, b);
	}

	public void setBorderColour(Colour borderColour) {
		this.borderColour.set(borderColour);
	}

	public void setScaleDriver(ValueDriver scaleDriver) {
		this.scaleDriver = scaleDriver;
	}

	public void setBorder(ValueDriver driver) {
		borderDriver = driver;
		solidBorder = true;
		glowBorder = false;
	}

	public void removeBorder() {
		solidBorder = false;
		glowBorder = false;
	}

	public void setGlowing(ValueDriver driver) {
		solidBorder = false;
		glowBorder = true;
		glowDriver = driver;
	}

	public void setAlphaDriver(ValueDriver driver) {
		alphaDriver = driver;
	}

	public float getScale() {
		return currentScale;
	}

	public int getNumberOfLines() {
		return numberOfLines;
	}

	public void setNumberOfLines(int number) {
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

	public float getOriginalWidth() {
		return originalWidth;
	}

	public float getCurrentWidth() {
		return originalWidth * currentScale;
	}

	public float getOriginalHeight() {
		return originalHeight;
	}

	public float getCurrentHeight() {
		return originalHeight * currentScale;
	}

	public float getCurrentX() {
		return currentX;
	}

	public float getCurrentY() {
		return currentY;
	}

	protected float getTransparency() {
		return currentAlpha;
	}
}
