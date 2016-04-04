package flounder.shaders;

import static org.lwjgl.opengl.GL20.*;

/**
 * Represents a float uniform type that can be loaded to the shader.
 */
public class UniformFloat extends Uniform {
	private float currentValue;

	public UniformFloat(final String name) {
		super(name);
	}

	/**
	 * Loads a float to the uniform if the value already on the GPU is not the same as the new value.
	 *
	 * @param value The new value.
	 */
	public void loadFloat(float value) {
		if (currentValue != value) {
			glUniform1f(super.getLocation(), value);
			currentValue = value;
		}
	}
}
