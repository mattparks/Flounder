package flounder.fonts;

import flounder.devices.*;
import flounder.framework.*;
import flounder.guis.*;
import flounder.loaders.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.visual.*;

/**
 * A object the represents a text in a GUI.
 */
public class TextObject extends ScreenObject {
	private String textString;
	private GuiAlign textAlign;

	private String newText;

	private int textMeshVao;
	private int vertexCount;
	private float lineMaxSize;
	private int numberOfLines;

	private FontType font;

	private Colour colour;
	private Colour borderColour;

	private boolean solidBorder;
	private boolean glowBorder;

	private ValueDriver glowDriver;
	private float glowSize;

	private ValueDriver borderDriver;
	private float borderSize;

	/**
	 * Creates a new ext object.
	 *
	 * @param parent The objects parent.
	 * @param position The objects position relative to the parents.
	 * @param text The text that will be set to this object,
	 * @param fontSize The initial size of the font (1 is the default).
	 * @param font The font type to be used in this text.
	 * @param maxLineLength The longest line length before the text is wrapped, 1.0 being 100& of the screen width when font size = 1.
	 * @param align How the text will align if wrapped.
	 */
	public TextObject(ScreenObject parent, Vector2f position, String text, float fontSize, FontType font, float maxLineLength, GuiAlign align) {
		super(parent, position, new Vector2f(1.0f, 1.0f));
		super.setMeshSize(new Vector2f());
		super.setScaleDriver(new ConstantDriver(fontSize));

		this.textString = text;
		this.textAlign = align;

		this.newText = null;

		this.textMeshVao = -1;
		this.vertexCount = -1;
		this.lineMaxSize = maxLineLength;
		this.numberOfLines = -1;

		this.font = font;

		this.colour = new Colour(0.0f, 0.0f, 0.0f, 1.0f);
		this.borderColour = new Colour(1.0f, 1.0f, 1.0f, 1.0f);

		this.solidBorder = false;
		this.glowBorder = false;

		this.glowDriver = new ConstantDriver(0.0f);
		this.glowSize = 0.0f;

		this.borderDriver = new ConstantDriver(0.0f);
		this.borderSize = 0.0f;

		font.loadText(this);
	}

	@Override
	public void updateObject() {
		if (isLoaded() && newText != null) {
			delete();
			textString = newText;
			font.loadText(this);
			newText = null;
		}

		switch (textAlign) {
			case LEFT:
				getPositionOffsets().set(getMeshSize().x * getScreenDimensions().x, 0.0f);
				break;
			case CENTRE:
				getPositionOffsets().set(0.0f, 0.0f);
				break;
			case RIGHT:
				getPositionOffsets().set(-getMeshSize().x * getScreenDimensions().x, 0.0f);
				break;
		}

		glowSize = glowDriver.update(Framework.getDelta());
		borderSize = borderDriver.update(Framework.getDelta());
	}

	/**
	 * Gets the string of text represented.
	 *
	 * @return The string of text.
	 */
	public String getTextString() {
		return textString;
	}

	/**
	 * Changed the current string in this text.
	 *
	 * @param newText The new text,
	 */
	public void setText(String newText) {
		if (!textString.equals(newText)) {
			this.newText = newText;
		}
	}

	/**
	 * Gets how the text should align.
	 *
	 * @return How the text should align.
	 */
	public GuiAlign getTextAlign() {
		return textAlign;
	}

	/**
	 * Gets the ID of the text's VAO, which contains all the vertex data for the quads on which the text will be rendered.
	 *
	 * @return The ID of the text's VAO.
	 */
	public int getMesh() {
		return textMeshVao;
	}

	/**
	 * Gets the total number of vertices of all the text's quads.
	 *
	 * @return The vertices count in the text's VAO.
	 */
	public int getVertexCount() {
		return this.vertexCount;
	}

	/**
	 * Sets the loaded mesh data for the text.
	 *
	 * @param vao The mesh VAO id.
	 * @param verticesCount The mesh vertex count.
	 */
	protected void setMeshInfo(int vao, int verticesCount) {
		this.textMeshVao = vao;
		this.vertexCount = verticesCount;
	}

	/**
	 * Gets the maximum length of a line of this text.
	 *
	 * @return The maximum length of a line.
	 */
	protected float getMaxLineSize() {
		return lineMaxSize;
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
	 * Sets the number of lines that this text covers (method used only in loading).
	 *
	 * @param number The new number of lines.
	 */
	protected void setNumberOfLines(int number) {
		this.numberOfLines = number;
	}

	/**
	 * Gets the font used by this text.
	 *
	 * @return The font used by this text.
	 */
	public FontType getFont() {
		return font;
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
	 * Sets the colour of the text.
	 *
	 * @param colour The new colour of the text.
	 */
	public void setColour(Colour colour) {
		this.colour.set(colour);
	}

	/**
	 * Gets the border colour of the text. This is used with border and glow drivers.
	 *
	 * @return The border colour of the text.
	 */
	public Colour getBorderColour() {
		return borderColour;
	}

	/**
	 * Sets the border colour of the text. This is used with border and glow drivers.
	 *
	 * @param borderColour The new border colour of the text.
	 */
	public void setBorderColour(Colour borderColour) {
		this.borderColour.set(borderColour);
	}

	/**
	 * Sets a new border driver, will disable glowing.
	 *
	 * @param driver The new border driver.
	 */
	public void setBorder(ValueDriver driver) {
		this.borderDriver = driver;
		this.solidBorder = true;
		this.glowBorder = false;
	}

	/**
	 * Sets a new glow driver, will disable solid borders.
	 *
	 * @param driver The new glow driver.
	 */
	public void setGlowing(ValueDriver driver) {
		this.glowDriver = driver;
		this.solidBorder = false;
		this.glowBorder = true;
	}

	/**
	 * Disables both solid borders and glow borders.
	 */
	public void removeBorder() {
		this.solidBorder = false;
		this.glowBorder = false;
	}

	/**
	 * Gets the calculated border size.
	 *
	 * @return The border size.
	 */
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

	/**
	 * Gets the size of the glow.
	 *
	 * @return The glow size.
	 */
	protected float getGlowSize() {
		if (solidBorder) {
			return calculateAntialiasSize();
		} else if (glowBorder) {
			return glowSize;
		} else {
			return 0.0f;
		}
	}

	/**
	 * Gets the distance field edge before antialias.
	 *
	 * @return The distance field edge.
	 */
	protected float calculateEdgeStart() {
		float size = super.getScale();
		return 1.0f / 300.0f * size + 137.0f / 300.0f;
	}

	/**
	 * Gets the distance field antialias distance.
	 *
	 * @return The distance field antialias distance.
	 */
	protected float calculateAntialiasSize() {
		float size = super.getScale();
		size = (size - 1.0f) / (1.0f + size / 4.0f) + 1.0f;
		return 0.1f / size;
	}

	/**
	 * Gets if the text has been loaded to OpenGL.
	 *
	 * @return If the text has been loaded to OpenGL.
	 */
	public boolean isLoaded() {
		return !textString.isEmpty() && textMeshVao != -1 && vertexCount != -1;
	}

	@Override
	public void deleteObject() {
		if (isLoaded()) {
			FlounderLoader.deleteVAOFromCache(textMeshVao);
			textMeshVao = -1;
			vertexCount = -1;
		}
	}
}
