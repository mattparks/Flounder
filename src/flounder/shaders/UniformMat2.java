package flounder.shaders;

import flounder.maths.matrices.*;
import org.lwjgl.*;

import java.nio.*;

import static org.lwjgl.opengl.GL20.*;

/**
 * Represents a Matrix2F uniform type that can be loaded to the shader.
 */
public class UniformMat2 extends Uniform {
	private final Matrix2f currentValue;
	private FloatBuffer floatBuffer;

	public UniformMat2(final String name) {
		super(name);
		currentValue = new Matrix2f();
		floatBuffer = BufferUtils.createFloatBuffer(4);
	}

	/**
	 * Loads a Matrix2F to the uniform if the value already on the GPU is not the same as the new value.
	 *
	 * @param value The new value.
	 */
	public void loadMat2(final Matrix2f value) {
		if (value != null && !currentValue.equals(value)) {
			floatBuffer.clear();
			value.store(floatBuffer);
			floatBuffer.flip();

			glUniformMatrix2fv(super.getLocation(), false, floatBuffer);
			currentValue.set(value);
		}
	}
}
