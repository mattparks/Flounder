package flounder.shaders;

import flounder.logger.*;
import flounder.processing.*;

import java.lang.ref.*;

/**
 * A class capable of setting up a {@link flounder.shaders.Shader}.
 */
public class ShaderBuilder {
	private String shaderName;
	private ShaderType[] shaderTypes;

	/**
	 * Creates a class to setup a Shader.
	 *
	 * @param shaderName The name of the shader to be loaded.
	 */
	protected ShaderBuilder(String shaderName) {
		this.shaderName = shaderName;
	}

	/**
	 * Sets the source shader file.
	 *
	 * @param shaderTypes The list of source shader file.
	 *
	 * @return this.
	 */
	public ShaderBuilder setShaderTypes(ShaderType... shaderTypes) {
		this.shaderTypes = shaderTypes;
		return this;
	}

	/**
	 * Creates a new shader, carries out the CPU loading, and loads to OpenGL from the loader thread.
	 *
	 * @return The shader that has been created.
	 */
	public Shader create() {
		SoftReference<Shader> ref = FlounderShaders.getLoaded().get(shaderName);
		Shader data = ref == null ? null : ref.get();

		if (data == null) {
			FlounderLogger.log(shaderName + " is being loaded into the shader builder right now!");
			FlounderShaders.getLoaded().remove(shaderName);
			data = new Shader();
			FlounderProcessors.sendRequest(new ShaderLoadRequest(data, this));
			FlounderShaders.getLoaded().put(shaderName, new SoftReference<>(data));
		}

		return data;
	}

	/**
	 * Gets the name of the shader to be loaded.
	 *
	 * @return The name of the shader to be loaded.
	 */
	public String getShaderName() {
		return shaderName;
	}

	/**
	 * Gets the array of shader types.
	 *
	 * @return The array of shader types.
	 */
	public ShaderType[] getShaderTypes() {
		return shaderTypes;
	}
}