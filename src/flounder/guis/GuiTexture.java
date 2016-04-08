package flounder.guis;

import flounder.engine.*;
import flounder.maths.vectors.*;
import flounder.textures.*;
import flounder.visual.*;

/**
 * A textured quad, making up part of a GUI component.
 */
public class GuiTexture {
	private final Vector2f position;
	private final Vector2f scale;
	private Texture texture;
	private ValueDriver alphaDriver;
	private int selectedRow;

	private float alpha;
	private boolean flipTexture;
	private final Vector2f textureOffset;

	public GuiTexture(final Texture texture) {
		this(texture, false);
	}

	public GuiTexture(final Texture texture, final boolean flip) {
		this.texture = texture;
		position = new Vector2f();
		scale = new Vector2f();
		alphaDriver = new ConstantDriver(1);
		selectedRow = 1;

		flipTexture = flip;
		textureOffset = new Vector2f();
	}

	public void update() {
		alpha = alphaDriver.update(FlounderEngine.getDelta());
	}

	public Texture getTexture() {
		return texture;
	}

	public void setTexture(final Texture texture) {
		this.texture = texture;
	}

	public Vector2f getPosition() {
		return position;
	}

	public void setPosition(final float x, final float y, final float width, final float height) {
		position.set(x, y);
		scale.set(width, height);
	}

	public Vector2f getScale() {
		return scale;
	}

	public void setAlphaDriver(final ValueDriver driver) {
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

	public boolean isFlipTexture() {
		return flipTexture;
	}

	@Override
	public String toString() {
		return "GuiTexture{" + "texture=" + texture + ", position=" + position + ", scale=" + scale + ", alphaDriver=" + alphaDriver + ", alpha=" + alpha + ", flipTexture=" + flipTexture + "}";
	}
}
