package flounder.shaders;

import flounder.maths.vectors.*;

import static org.lwjgl.opengl.GL20.*;

/**
 * Represents a 2 value vector uniform type that can be loaded to the shader.
 */
public class UniformVec2 extends Uniform {
	private float currentX;
	private float currentY;

	public UniformVec2(String name) {
		super(name);
	}

	/**
	 * Loads a Vector2f to the uniform if the value already on the GPU is not the same as the new value.
	 *
	 * @param vector The new vector.
	 */
	public void loadVec2(Vector2f vector) {
		loadVec2(vector.getX(), vector.getY());
	}

	/**
	 * Loads a x and y value to the uniform if the value already on the GPU is not the same as the new value.
	 *
	 * @param x The new x value.
	 * @param y The new y value.
	 */
	public void loadVec2(float x, float y) {
		if (x != currentX || y != currentY) {
			glUniform2f(super.getLocation(), x, y);
			currentX = x;
			currentY = y;
		}
	}
}
