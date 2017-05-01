package flounder.shaders;

import flounder.maths.vectors.*;

/**
 * Represents a 2 value vector uniform type that can be loaded to the shader.
 */
public class UniformVec2 extends Uniform {
	private Vector2f current;

	public UniformVec2(String name, ShaderObject shader) {
		super(name, shader);
		this.current = new Vector2f();
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
		if (x != current.x || y != current.y) {
			current.set(x, y);
			FlounderShaders.get().storeVectorData(super.getLocation(), current);
		}
	}
}
