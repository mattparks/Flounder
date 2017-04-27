package flounder.shaders;

/**
 * Represents a float uniform type that can be loaded to the shader.
 */
public class UniformFloat extends Uniform {
	private float current;

	public UniformFloat(String name, ShaderObject shader) {
		super(name, shader);
	}

	/**
	 * Loads a float to the uniform if the value already on the GPU is not the same as the new value.
	 *
	 * @param value The new value.
	 */
	public void loadFloat(float value) {
		if (current != value) {
			current = value;
			FlounderShaders.storeSimpleData(super.getLocation(), value);
		}
	}
}
