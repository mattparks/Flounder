package flounder.guis;

import flounder.engine.*;
import flounder.maths.vectors.*;
import flounder.textures.*;
import flounder.visual.*;

/**
 * A textured quad, making up part of a GUI component.
 */
public class GuiTexture {
	private Texture texture;

	private Vector2f position;
	private Vector2f scale;

	private ValueDriver alphaDriver;

	private float alpha;
	private boolean flipTexture;

	public GuiTexture(Texture texture) {
		this(texture, false);
	}

	public GuiTexture(Texture texture, boolean flip) {
		this.texture = texture;
		position = new Vector2f();
		scale = new Vector2f();
		alphaDriver = new ConstantDriver(1);
		flipTexture = flip;
	}

	public void update() {
		alpha = alphaDriver.update(FlounderEngine.getDelta());
	}

	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
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

	public void setAlphaDriver(ValueDriver driver) {
		alphaDriver = driver;
	}

	public float getAlpha() {
		return alpha;
	}

	public boolean isFlipTexture() {
		return flipTexture;
	}
}
