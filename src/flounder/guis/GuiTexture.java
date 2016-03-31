package flounder.guis;

import flounder.engine.*;
import flounder.maths.vectors.*;
import flounder.textures.*;
import flounder.visual.*;

/**
 * A textured quad, making up part of a GUI component.
 */
public class GuiTexture {
	private Texture m_texture;

	private Vector2f m_position;
	private Vector2f m_scale;

	private ValueDriver m_alphaDriver;

	private float m_alpha;
	private boolean m_flipTexture;

	public GuiTexture(Texture texture) {
		this(texture, false);
	}

	public GuiTexture(Texture texture, boolean flip) {
		m_texture = texture;
		m_position = new Vector2f();
		m_scale = new Vector2f();
		m_alphaDriver = new ConstantDriver(1);
		m_flipTexture = flip;
	}

	public void update() {
		m_alpha = m_alphaDriver.update(FlounderEngine.getDelta());
	}

	public Texture getTexture() {
		return m_texture;
	}

	public void setTexture(Texture texture) {
		m_texture = texture;
	}

	public Vector2f getPosition() {
		return m_position;
	}

	public void setPosition(float x, float y, float width, float height) {
		m_position.set(x, y);
		m_scale.set(width, height);
	}

	public Vector2f getScale() {
		return m_scale;
	}

	public void setAlphaDriver(ValueDriver driver) {
		m_alphaDriver = driver;
	}

	public float getAlpha() {
		return m_alpha;
	}

	public boolean isFlipTexture() {
		return m_flipTexture;
	}
}
