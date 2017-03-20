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
		this.texture = texture;
		this.flipTexture = false;
		this.selectedRow = selectedRow;

		this.textureOffset = new Vector2f();
		this.colourOffset = new Colour();
	}

	@Override
	public void updateObject() {
	}

	public TextureObject getTexture() {
		return texture;
	}

	public void setTexture(TextureObject texture) {
		this.texture = texture;
	}

	public boolean isFlipTexture() {
		return flipTexture;
	}

	public void setFlipTexture(boolean flipTexture) {
		this.flipTexture = flipTexture;
	}

	public int getSelectedRow() {
		return selectedRow;
	}

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

	public Colour getColourOffset() {
		return colourOffset;
	}

	public void setColourOffset(Colour colourOffset) {
		this.colourOffset.set(colourOffset);
	}

	@Override
	public void deleteObject() {
	}
}
