package flounder.fonts;

/**
 * Simple data structure class holding information about a certain glyph in the font texture atlas. All sizes are for a font-size of 1.
 */
public class Character {
	private final int m_id;
	private final double m_xTextureCoord;
	private final double m_yTextureCoord;
	private final double m_xMaxTextureCoord;
	private final double m_yMaxTextureCoord;
	private final double m_xOffset;
	private final double m_yOffset;
	private final double m_sizeX;
	private final double m_sizeY;
	private final double m_xAdvance;

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
		m_id = id;
		m_xTextureCoord = xTextureCoord;
		m_yTextureCoord = yTextureCoord;
		m_xOffset = xOffset;
		m_yOffset = yOffset;
		m_sizeX = sizeX;
		m_sizeY = sizeY;
		m_xMaxTextureCoord = xTexSize + xTextureCoord;
		m_yMaxTextureCoord = yTexSize + yTextureCoord;
		m_xAdvance = xAdvance;
	}

	protected int getId() {
		return m_id;
	}

	protected double getXTextureCoord() {
		return m_xTextureCoord;
	}

	protected double getYTextureCoord() {
		return m_yTextureCoord;
	}

	protected double getXMaxTextureCoord() {
		return m_xMaxTextureCoord;
	}

	protected double getYMaxTextureCoord() {
		return m_yMaxTextureCoord;
	}

	protected double getXOffset() {
		return m_xOffset;
	}

	protected double getYOffset() {
		return m_yOffset;
	}

	protected double getSizeX() {
		return m_sizeX;
	}

	protected double getSizeY() {
		return m_sizeY;
	}

	protected double getXAdvance() {
		return m_xAdvance;
	}
}
