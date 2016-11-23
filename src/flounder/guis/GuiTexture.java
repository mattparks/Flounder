package flounder.guis;

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
	private Vector2f scale;
	private Vector2f textureOffset;
	private Colour colourOffset;

	private Texture texture;
	private ValueDriver alphaDriver;
	private int selectedRow;
	private float alpha;
	private boolean flipTexture;

	public GuiTexture(Texture texture) {
		this(texture, false);
	}

	public GuiTexture(Texture texture, boolean flip) {
		this.texture = texture;
		position = new Vector2f();
		scale = new Vector2f();
		textureOffset = new Vector2f();
		colourOffset = new Colour();

		alphaDriver = new ConstantDriver(1);
		selectedRow = 1;

		flipTexture = flip;
	}

	public void update() {
		alpha = alphaDriver.update(FlounderFramework.getDelta());
	}

	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	public Colour getColourOffset() {
		return colourOffset;
	}

	public void setColourOffset(Colour colour) {
		colourOffset.set(colour);
	}

	public Vector2f getPosition() {
		return position;
	}

	public void setPosition(float x, float y, float width, float height) {
		position.set(x, y);
		scale.set(width, height);
	}

	public Vector2f getScale() {
		return scale;
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
		int column = selectedRow % texture.getNumberOfRows();
		int row = selectedRow / texture.getNumberOfRows();
		return textureOffset.set((float) column / texture.getNumberOfRows(), (float) row / texture.getNumberOfRows());
	}

	@Override
	public String toString() {
		return "GuiTexture{" + "texture=" + texture + ", position=" + position + ", scale=" + scale + ", alphaDriver=" + alphaDriver + ", alpha=" + alpha + ", flipTexture=" + flipTexture + "}";
	}
}
