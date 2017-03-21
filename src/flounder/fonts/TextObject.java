package flounder.fonts;

import flounder.guis.*;
import flounder.loaders.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.visual.*;

public class TextObject extends ScreenObject {
	private String textString;

	private int textMeshVao;
	private int vertexCount;
	private Colour colour = new Colour(0f, 0f, 0f);

	private Vector2f position;
	private float lineMaxSize;
	private int numberOfLines;

	private FontType font;

	private boolean centerText = false;

	public TextObject(ScreenObject parent, Vector2f position, String text, float fontSize, FontType font, float maxLineLength, boolean centered) {
		super(parent, position, new Vector2f(1.0f, 1.0f));
		super.setScaleDriver(new ConstantDriver(fontSize));

		this.textString = text;
		this.font = font;
		this.position = position;
		this.lineMaxSize = maxLineLength;
		this.centerText = centered;

		font.loadText(this);
	}

	@Override
	public void updateObject() {
	}

	protected void setMeshInfo(int vao, int verticesCount) {
		this.textMeshVao = vao;
		this.vertexCount = verticesCount;
	}

	/**
	 * @return The font used by this text.
	 */
	public FontType getFont() {
		return font;
	}

	/**
	 * Set the colour of the text.
	 *
	 * @param r Red value, between 0 and 1.
	 * @param g Green value, between 0 and 1.
	 * @param b Blue value, between 0 and 1.
	 */
	public void setColour(float r, float g, float b) {
		colour.set(r, g, b);
	}

	/**
	 * Gets the colour of the text.
	 *
	 * @return The colour of the text.
	 */
	public Colour getColour() {
		return colour;
	}

	/**
	 * Gets the number of lines of text. This is determined when the text is  loaded, based on the length of the text and the max line length that is set.
	 *
	 * @return The number of lines of text
	 */
	public int getNumberOfLines() {
		return numberOfLines;
	}

	/**
	 * Gets the position of the top-left corner of the text in screen-space. (0, 0) is the top left corner of the screen, (1, 1) is the bottom right.
	 *
	 * @return The position.
	 */
	public Vector2f getPosition() {
		return position;
	}

	/**
	 * @return The ID of the text's VAO, which contains all the vertex data for the quads on which the text will be rendered.
	 */
	public int getMesh() {
		return textMeshVao;
	}

	/**
	 * @return The total number of vertices of all the text's quads.
	 */
	public int getVertexCount() {
		return this.vertexCount;
	}

	/**
	 * Sets the number of lines that this text covers (method used only in loading).
	 *
	 * @param number
	 */
	protected void setNumberOfLines(int number) {
		this.numberOfLines = number;
	}

	/**
	 * @return {@code true} if the text should be centered.
	 */
	protected boolean isCentered() {
		return centerText;
	}

	/**
	 * @return The maximum length of a line of this text.
	 */
	protected float getMaxLineSize() {
		return lineMaxSize;
	}

	/**
	 * @return The string of text.
	 */
	protected String getTextString() {
		return textString;
	}

	protected float calculateEdgeStart() {
		float size = super.getScale();
		return 1.0f / 300.0f * size + 137.0f / 300.0f;
	}

	protected float calculateAntialiasSize() {
		float size = super.getScale();
		size = (size - 1.0f) / (1.0f + size / 4.0f) + 1.0f;
		return 0.1f / size;
	}

	@Override
	public void deleteObject() {
	}
}
