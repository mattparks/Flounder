package com.flounder.shaders;

import com.flounder.maths.matrices.*;
import com.flounder.platform.*;

import java.nio.*;

/**
 * Represents a Matrix4F uniform type that can be loaded to the shader.
 */
public class UniformMat4 extends Uniform {
	private Matrix4f current;
	private FloatBuffer floatBuffer;

	public UniformMat4(String name, ShaderObject shader) {
		super(name, shader);
		this.current = new Matrix4f();
		this.floatBuffer = FlounderPlatform.get().createFloatBuffer(16);
	}

	/**
	 * Loads a Matrix4F to the uniform if the value already on the GPU is not the same as the new value.
	 *
	 * @param value The new value.
	 */
	public void loadMat4(Matrix4f value) {
		if (value != null && !current.equals(value)) {
			current.set(value);
			FlounderShaders.get().storeMatrixData(super.getLocation(), floatBuffer, value);
		}
	}
}
