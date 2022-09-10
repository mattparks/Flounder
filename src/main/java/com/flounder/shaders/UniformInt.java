package com.flounder.shaders;

/**
 * Represents a int uniform type that can be loaded to the shader.
 */
public class UniformInt extends Uniform {
	private int current;

	public UniformInt(String name, ShaderObject shader) {
		super(name, shader);
	}

	/**
	 * Loads a float to the uniform if the value already on the GPU is not the same as the new value.
	 *
	 * @param value The new value.
	 */
	public void loadInt(int value) {
		if (current != value) {
			current = value;
			FlounderShaders.get().storeSimpleData(super.getLocation(), value);
		}
	}
}
