package flounder.shaders;

import static org.lwjgl.opengl.GL20.*;

/**
 * Represents a float uniform type that can be loaded to the shader.
 */
public class UniformBool extends Uniform {
	private boolean currentValue;

	public UniformBool(String name) {
		super(name);
	}

	/**
	 * Loads a boolean to the uniform if the value already on the GPU is not the same as the new value.
	 *
	 * @param value The new value.
	 */
	public void loadBoolean(boolean value) {
		if (currentValue != value) {
			glUniform1f(super.getLocation(), value ? 1.0f : 0.0f);
			currentValue = value;
		}
	}
}
