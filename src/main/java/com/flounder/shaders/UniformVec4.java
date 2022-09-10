package com.flounder.shaders;

import com.flounder.maths.*;
import com.flounder.maths.vectors.*;

/**
 * Represents a 4 value vector uniform type that can be loaded to the shader.
 */
public class UniformVec4 extends Uniform {
	private Vector4f current;

	public UniformVec4(String name, ShaderObject shader) {
		super(name, shader);
		this.current = new Vector4f();
	}

	/**
	 * Loads a Vector4f to the uniform if the value already on the GPU is not the same as the new value.
	 *
	 * @param vector The new vector.
	 */
	public void loadVec4(Vector4f vector) {
		loadVec4(vector.x, vector.y, vector.z, vector.w);
	}

	/**
	 * Loads a Colour to the uniform if the value already on the GPU is not the same as the new value.
	 *
	 * @param colour The new colour.
	 */
	public void loadVec4(Colour colour) {
		loadVec4(colour.r, colour.g, colour.b, colour.a);
	}

	/**
	 * Loads a x, y, z and w value to the uniform if the value already on the GPU is not the same as the new value.
	 *
	 * @param x The new x value.
	 * @param y The new y value.
	 * @param z The new z value.
	 * @param w The new w value.
	 */
	public void loadVec4(float x, float y, float z, float w) {
		if (x != current.x || y != current.y || z != current.z || w != current.w) {
			current.set(x, y, z, w);
			FlounderShaders.get().storeVectorData(super.getLocation(), current);
		}
	}
}
