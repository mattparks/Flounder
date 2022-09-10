package com.flounder.shaders;

/**
 * Represents a float uniform type that can be loaded to the shader.
 */
public class UniformBool extends Uniform {
	private boolean current;

	public UniformBool(String name, ShaderObject shader) {
		super(name, shader);
	}

	/**
	 * Loads a boolean to the uniform if the value already on the GPU is not the same as the new value.
	 *
	 * @param value The new value.
	 */
	public void loadBoolean(boolean value) {
		if (current != value) {
			current = value;
			FlounderShaders.get().storeSimpleData(super.getLocation(), value);
		}
	}
}
