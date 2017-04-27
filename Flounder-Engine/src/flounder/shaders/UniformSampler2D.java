package flounder.shaders;

/**
 * Represents a texture sampler uniform type that can be loaded to the shader.
 */
public class UniformSampler2D extends Uniform {
	private int current;

	public UniformSampler2D(String name, ShaderObject shader) {
		super(name, shader);
	}

	/**
	 * Loads a int sampler to the uniform if the value already on the GPU is not the same as the new value.
	 *
	 * @param value The new value.
	 */
	public void loadTexUnit(int value) {
		if (current != value) {
			current = value;
			FlounderShaders.storeSimpleData(super.getLocation(), value);
		}
	}
}
