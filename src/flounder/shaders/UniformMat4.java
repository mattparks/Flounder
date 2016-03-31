package flounder.shaders;

import flounder.maths.matrices.*;
import org.lwjgl.*;

import java.nio.*;

import static org.lwjgl.opengl.GL20.*;

/**
 * Represents a Matrix4F uniform type that can be loaded to the shader.
 */
public class UniformMat4 extends Uniform {
	private Matrix4f currentValue;

	public UniformMat4(String name) {
		super(name);
		currentValue = new Matrix4f();
	}

	/**
	 * Loads a Matrix4F to the uniform if the value already on the GPU is not the same as the new value.
	 *
	 * @param value The new value.
	 */
	public void loadMat4(Matrix4f value) {
		if (value != null && !currentValue.equals(value)) {
			FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
			value.store(matrixBuffer);
			matrixBuffer.flip();
			currentValue.set(value);
			glUniformMatrix4fv(super.getLocation(), false, matrixBuffer);
		}
	}
}
