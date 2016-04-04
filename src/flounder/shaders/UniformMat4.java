package flounder.shaders;

import flounder.maths.matrices.*;
import org.lwjgl.*;

import java.nio.*;

import static org.lwjgl.opengl.GL20.*;

/**
 * Represents a Matrix4F uniform type that can be loaded to the shader.
 */
public class UniformMat4 extends Uniform {
	private final Matrix4f currentValue;
	private FloatBuffer floatBuffer;

	public UniformMat4(final String name) {
		super(name);
		currentValue = new Matrix4f();
		floatBuffer = BufferUtils.createFloatBuffer(16);
	}

	/**
	 * Loads a Matrix4F to the uniform if the value already on the GPU is not the same as the new value.
	 *
	 * @param value The new value.
	 */
	public void loadMat4(final Matrix4f value) {
		if (value != null && !currentValue.equals(value)) {
			floatBuffer.clear();
			value.store(floatBuffer);
			floatBuffer.flip();

			glUniformMatrix4fv(super.getLocation(), false, floatBuffer);
			currentValue.set(value);
		}
	}
}
