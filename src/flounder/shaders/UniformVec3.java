package flounder.shaders;

import flounder.lights.*;
import flounder.maths.*;
import flounder.maths.vectors.*;

import static org.lwjgl.opengl.GL20.*;

/**
 * Represents a 3 value vector uniform type that can be loaded to the shader.
 */
public class UniformVec3 extends Uniform {
	private float currentX;
	private float currentY;
	private float currentZ;

	public UniformVec3(String name, ShaderObject shader) {
		super(name, shader);
	}

	/**
	 * Loads a Vector3f to the uniform if the value already on the GPU is not the same as the new value.
	 *
	 * @param vector The new vector.
	 */
	public void loadVec3(Vector3f vector) {
		loadVec3(vector.x, vector.y, vector.z);
	}

	/**
	 * Loads a x, y and z value to the uniform if the value already on the GPU is not the same as the new value.
	 *
	 * @param x The new x value.
	 * @param y The new y value.
	 * @param z The new z value.
	 */
	public void loadVec3(float x, float y, float z) {
		if (x != currentX || y != currentY || z != currentZ) {
			glUniform3f(super.getLocation(), x, y, z);
			currentX = x;
			currentY = y;
			currentZ = z;
		}
	}

	/**
	 * Loads a Colour to the uniform if the value already on the GPU is not the same as the new value.
	 *
	 * @param colour The new colour.
	 */
	public void loadVec3(Colour colour) {
		loadVec3(colour.r, colour.g, colour.b);
	}

	/**
	 * Loads a Attenuation factor to the uniform if the value already on the GPU is not the same as the new value.
	 *
	 * @param attenuation The new attenuation.
	 */
	public void loadVec3(Attenuation attenuation) {
		loadVec3(attenuation.constant, attenuation.linear, attenuation.exponent);
	}
}
