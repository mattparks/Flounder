package flounder.shaders;

import flounder.maths.matrices.*;
import org.lwjgl.*;

import java.nio.*;

import static org.lwjgl.opengl.GL20.*;

/**
 * Represents a Matrix2F uniform type that can be loaded to the shader.
 */
public class UniformMat2 extends Uniform {
	private Matrix2f currentValue;

	public UniformMat2(String name) {
		super(name);
		currentValue = new Matrix2f();
	}

	/**
	 * Loads a Matrix2F to the uniform if the value already on the GPU is not the same as the new value.
	 *
	 * @param value The new value.
	 */
	public void loadMat2(Matrix2f value) {
		if (value != null && !currentValue.equals(value)) {
			FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
			value.store(matrixBuffer);
			matrixBuffer.flip();
			currentValue.set(value);
			glUniformMatrix2fv(super.getLocation(), false, matrixBuffer);
		}
	}
}
