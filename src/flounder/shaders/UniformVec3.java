package flounder.shaders;

import flounder.lights.*;
import flounder.maths.*;
import flounder.maths.vectors.*;

/**
 * Represents a 3 value vector uniform type that can be loaded to the shader.
 */
public class UniformVec3 extends Uniform {
	private Vector3f current;

	public UniformVec3(String name, ShaderObject shader) {
		super(name, shader);
		this.current = new Vector3f();
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
		if (x != current.x || y != current.y || z != current.z) {
			current.set(x, y, z);
			FlounderShaders.get().storeVectorData(super.getLocation(), current);
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
