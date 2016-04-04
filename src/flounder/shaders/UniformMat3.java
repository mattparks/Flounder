package flounder.shaders;

import flounder.maths.matrices.*;
import org.lwjgl.*;

import java.nio.*;

import static org.lwjgl.opengl.GL20.*;

/**
 * Represents a Matrix3F uniform type that can be loaded to the shader.
 */
public class UniformMat3 extends Uniform {
	private final Matrix3f currentValue;
	private FloatBuffer floatBuffer;

	public UniformMat3(final String name) {
		super(name);
		currentValue = new Matrix3f();
		floatBuffer = BufferUtils.createFloatBuffer(9);
	}

	/**
	 * Loads a Matrix3F to the uniform if the value already on the GPU is not the same as the new value.
	 *
	 * @param value The new value.
	 */
	public void loadMat3(final Matrix3f value) {
		if (value != null && !currentValue.equals(value)) {
			floatBuffer.clear();
			value.store(floatBuffer);
			floatBuffer.flip();

			glUniformMatrix3fv(super.getLocation(), false, floatBuffer);
			currentValue.set(value);
		}
	}
}
