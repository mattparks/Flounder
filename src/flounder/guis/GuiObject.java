package flounder.guis;

import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.textures.*;

/**
 * A object the represents a texture in a GUI.
 */
public class GuiObject extends ScreenObject {
	private TextureObject texture;
	private boolean flipTexture;
	private int selectedRow;

	private Vector2f textureOffset;
	private Colour colourOffset;

	/**
	 * Creates a new GUI object.
	 *
	 * @param parent The objects parent.
	 * @param position The objects position relative to the parents.
	 * @param dimensions The objects dimensions.
	 * @param texture The objects texture.
	 * @param selectedRow The default row of the texture to render from.
	 */
	public GuiObject(ScreenObject parent, Vector2f position, Vector2f dimensions, TextureObject texture, int selectedRow) {
		super(parent, position, dimensions);
		super.setMeshSize(new Vector2f((FlounderGuis.POSITION_MIN + FlounderGuis.POSITION_MAX) / 2.0f, (FlounderGuis.POSITION_MIN + FlounderGuis.POSITION_MAX) / 2.0f));

		this.texture = texture;
		this.flipTexture = false;
		this.selectedRow = selectedRow;

		this.textureOffset = new Vector2f();
		this.colourOffset = new Colour();
	}

	@Override
	public void updateObject() {
	}

	@Override
	public void deleteObject() {
	}

	/**
	 * Gets the texture used in this GUI object.
	 *
	 * @return The texture used.
	 */
	public TextureObject getTexture() {
		return texture;
	}

	/**
	 * Sets the texture to be used.
	 *
	 * @param texture The new texture.
	 */
	public void setTexture(TextureObject texture) {
		this.texture = texture;
	}

	/**
	 * Gets if the texture is flipped across the y axis.
	 *
	 * @return If the texture is flipped.
	 */
	public boolean isFlipTexture() {
		return flipTexture;
	}

	/**
	 * Sets the texture to flip across the y axis.
	 *
	 * @param flipTexture If the texture should be flipped.
	 */
	public void setFlipTexture(boolean flipTexture) {
		this.flipTexture = flipTexture;
	}

	/**
	 * Gets the row selected in the texture atlas.
	 *
	 * @return The selected row.
	 */
	public int getSelectedRow() {
		return selectedRow;
	}

	/**
	 * Sets a selected row in the texture index.
	 *
	 * @param selectedRow The new selected row.
	 */
	public void setSelectedRow(int selectedRow) {
		this.selectedRow = selectedRow;
	}

	/**
	 * Gets the textures coordinate offset that is used in rendering the texture.
	 *
	 * @return The coordinate offset used in rendering.
	 */
	public Vector2f getTextureOffset() {
		int numberOfRows = texture != null ? texture.getNumberOfRows() : 1;
		int column = selectedRow % numberOfRows;
		int row = selectedRow / numberOfRows;
		return textureOffset.set((float) column / numberOfRows, (float) row / numberOfRows);
	}

	/**
	 * Gets the colour offset (added to the texture colour).
	 *
	 * @return The texture colour offset.
	 */
	public Colour getColourOffset() {
		return colourOffset;
	}

	/**
	 * Sets the colour offset (added to the texture colour).
	 *
	 * @param colourOffset The new texture colour offset.
	 */
	public void setColourOffset(Colour colourOffset) {
		this.colourOffset.set(colourOffset);
	}
}
