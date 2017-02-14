package flounder.guis;

import flounder.devices.*;
import flounder.framework.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.textures.*;
import flounder.visual.*;

/**
 * A textured quad, making up part of a GUI component.
 */
public class GuiTexture {
	private Vector2f position;
	private float rotation;
	private Vector2f scale;
	private Vector2f textureOffset;
	private Colour colourOffset;

	private TextureObject texture;
	private ValueDriver alphaDriver;
	private int selectedRow;
	private float alpha;
	private boolean flipTexture;

	public GuiTexture(TextureObject texture) {
		this(texture, false);
	}

	public GuiTexture(TextureObject texture, boolean flip) {
		this.position = new Vector2f();
		this.rotation = 0.0f;
		this.scale = new Vector2f();
		this.textureOffset = new Vector2f();
		this.colourOffset = new Colour();

		this.texture = texture;
		this.alphaDriver = new ConstantDriver(1);
		this.selectedRow = 1;

		this.flipTexture = flip;
	}

	public void update() {
		alpha = alphaDriver.update(FlounderFramework.getDelta());
	}

	/**
	 * @return {@code true} if the mouse cursor is currently over this GUI texture.
	 */
	public boolean isMouseOver() {
		// TODO: Fix maths?
		float positionX = position.x / FlounderDisplay.getAspectRatio();
		float positionY = position.y;

		//	if (FlounderMouse.isDisplaySelected() && FlounderDisplay.isFocused()) {
		if (FlounderGuis.getSelector().getCursorX() >= positionX - (scale.x / 2.0f) && FlounderGuis.getSelector().getCursorX() <= positionX + (scale.x / 2.0f)) {
			if (FlounderGuis.getSelector().getCursorY() >= positionY - (scale.y / 2.0f) && FlounderGuis.getSelector().getCursorY() <= positionY + (scale.y / 2.0f)) {
				return true;
			}
		}
		//	}

		return false;
	}

	public Vector2f getPosition() {
		return position;
	}

	public void setPosition(float x, float y, float width, float height) {
		position.set(x, y);
		scale.set(width, height);
	}

	public float getRotation() {
		return rotation;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public Vector2f getScale() {
		return scale;
	}

	public Colour getColourOffset() {
		return colourOffset;
	}

	public void setColourOffset(Colour colour) {
		colourOffset.set(colour);
	}

	public TextureObject getTexture() {
		return texture;
	}

	public void setTexture(TextureObject texture) {
		this.texture = texture;
	}

	public ValueDriver getAlphaDriver() {
		return alphaDriver;
	}

	public void setAlphaDriver(ValueDriver driver) {
		alphaDriver = driver;
	}

	public float getAlpha() {
		return alpha;
	}

	public int getSelectedRow() {
		return selectedRow;
	}

	public void setSelectedRow(int selectedRow) {
		this.selectedRow = selectedRow;
	}

	public boolean isFlipTexture() {
		return flipTexture;
	}

	public void setFlipTexture(boolean flipTexture) {
		this.flipTexture = flipTexture;
	}

	/**
	 * Gets the textures coordinate offset that is used in rendering the texture.
	 *
	 * @return The coordinate offset used in rendering.
	 */
	public Vector2f getTextureOffset() {
		int column = selectedRow % texture.getNumberOfRows();
		int row = selectedRow / texture.getNumberOfRows();
		return textureOffset.set((float) column / texture.getNumberOfRows(), (float) row / texture.getNumberOfRows());
	}

	@Override
	public String toString() {
		return "GuiTexture{" + "texture=" + texture + ", position=" + position + ", rotation=" + rotation + ", scale=" + scale + ", alphaDriver=" + alphaDriver + ", alpha=" + alpha + ", flipTexture=" + flipTexture + "}";
	}
}
