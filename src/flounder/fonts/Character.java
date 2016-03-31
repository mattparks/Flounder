package flounder.fonts;

/**
 * Simple data structure class holding information about a certain glyph in the font texture atlas. All sizes are for a font-size of 1.
 */
public class Character {
	private final int id;
	private final double xTextureCoord;
	private final double yTextureCoord;
	private final double xMaxTextureCoord;
	private final double yMaxTextureCoord;
	private final double xOffset;
	private final double yOffset;
	private final double sizeX;
	private final double sizeY;
	private final double xAdvance;

	/**
	 * @param id The ASCII value of the character.
	 * @param xTextureCoord The x texture coordinate for the top left corner of the character in the texture atlas.
	 * @param yTextureCoord The y texture coordinate for the top left corner of the character in the texture atlas.
	 * @param xTexSize The width of the character in the texture atlas.
	 * @param yTexSize The height of the character in the texture atlas.
	 * @param xOffset The x distance from the cursor to the left edge of the character's quad.
	 * @param yOffset The y distance from the cursor to the top edge of the character's quad.
	 * @param sizeX The width of the character's quad in screen space.
	 * @param sizeY The height of the character's quad in screen space.
	 * @param xAdvance How much the cursor will move forward after this character.
	 */
	protected Character(final int id, final double xTextureCoord, final double yTextureCoord, final double xTexSize, final double yTexSize, final double xOffset, final double yOffset, final double sizeX, final double sizeY, final double xAdvance) {
		this.id = id;
		this.xTextureCoord = xTextureCoord;
		this.yTextureCoord = yTextureCoord;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		xMaxTextureCoord = xTexSize + xTextureCoord;
		yMaxTextureCoord = yTexSize + yTextureCoord;
		this.xAdvance = xAdvance;
	}

	protected int getId() {
		return id;
	}

	protected double getXTextureCoord() {
		return xTextureCoord;
	}

	protected double getYTextureCoord() {
		return yTextureCoord;
	}

	protected double getXMaxTextureCoord() {
		return xMaxTextureCoord;
	}

	protected double getYMaxTextureCoord() {
		return yMaxTextureCoord;
	}

	protected double getXOffset() {
		return xOffset;
	}

	protected double getYOffset() {
		return yOffset;
	}

	protected double getSizeX() {
		return sizeX;
	}

	protected double getSizeY() {
		return sizeY;
	}

	protected double getXAdvance() {
		return xAdvance;
	}
}
