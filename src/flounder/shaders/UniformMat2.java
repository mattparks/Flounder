package flounder.shaders;

import flounder.maths.matrices.*;
import flounder.platform.*;

import java.nio.*;

/**
 * Represents a Matrix2F uniform type that can be loaded to the shader.
 */
public class UniformMat2 extends Uniform {
	private Matrix2f current;
	private FloatBuffer floatBuffer;

	public UniformMat2(String name, ShaderObject shader) {
		super(name, shader);
		this.current = new Matrix2f();
		this.floatBuffer = FlounderPlatform.createFloatBuffer(4);
	}

	/**
	 * Loads a Matrix2F to the uniform if the value already on the GPU is not the same as the new value.
	 *
	 * @param value The new value.
	 */
	public void loadMat2(Matrix2f value) {
		if (value != null && !current.equals(value)) {
			current.set(value);
			FlounderShaders.storeMatrixData(super.getLocation(), floatBuffer, value);
		}
	}
}
