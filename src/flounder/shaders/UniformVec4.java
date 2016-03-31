package flounder.shaders;

import flounder.maths.*;
import flounder.maths.vectors.*;

import static org.lwjgl.opengl.GL20.*;

/**
 * Represents a 4 value vector uniform type that can be loaded to the shader.
 */
public class UniformVec4 extends Uniform {
	private float currentX;
	private float currentY;
	private float currentZ;
	private float currentW;

	public UniformVec4(String name) {
		super(name);
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
		if (x != currentX || y != currentY || z != currentZ || w != currentW) {
			currentX = x;
			currentY = y;
			currentZ = z;
			currentW = w;
			glUniform4f(super.getLocation(), x, y, z, w);
		}
	}
}
