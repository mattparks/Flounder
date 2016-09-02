package flounder.shaders;

import flounder.engine.*;

import java.lang.ref.*;
import java.util.*;

/**
 * A class capable of setting up a {@link flounder.shaders.Shader}.
 */
public class ShaderBuilder {
	private static Map<String, SoftReference<Shader>> loadedModels = new HashMap<>();

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
	 * Creates a new shader, carries out the CPU loading, and loads to OpenGL.
	 *
	 * @return The shader that has been created.
	 */
	public Shader create() {
		SoftReference<Shader> ref = loadedModels.get(shaderName);
		Shader data = ref == null ? null : ref.get();

		if (data == null) {
			FlounderEngine.getLogger().log(shaderName + " is being loaded into the shader builder right now!");
			loadedModels.remove(shaderName);
			data = new Shader();
			ShaderLoadRequest request = new ShaderLoadRequest(data, this, false);
			request.doResourceRequest();
			request.executeGlRequest();
			loadedModels.put(shaderName, new SoftReference<>(data));
		}

		return data;
	}

	/**
	 * Creates a new shader and sends it to be loaded by the loader thread.
	 *
	 * @return The model.
	 */
	public Shader createInBackground() {
		SoftReference<Shader> ref = loadedModels.get(shaderName);
		Shader data = ref == null ? null : ref.get();

		if (data == null) {
			FlounderEngine.getLogger().log(shaderName + " is being loaded into the shader builder in the background!");
			loadedModels.remove(shaderName);
			data = new Shader();
			FlounderEngine.getProcessors().sendRequest(new ShaderLoadRequest(data, this, true));
			loadedModels.put(shaderName, new SoftReference<>(data));
		}

		return data;
	}

	/**
	 * Creates a new shader, carries out the CPU loading, and sends to the main thread for GL loading.
	 *
	 * @return The shader.
	 */
	public Shader createInSecondThread() {
		SoftReference<Shader> ref = loadedModels.get(shaderName);
		Shader data = ref == null ? null : ref.get();

		if (data == null) {
			FlounderEngine.getLogger().log(shaderName + " is being loaded into the shader builder in separate thread!");
			loadedModels.remove(shaderName);
			data = new Shader();
			ShaderLoadRequest request = new ShaderLoadRequest(data, this, false);
			request.doResourceRequest();
			FlounderEngine.getProcessors().sendGLRequest(request);
			loadedModels.put(shaderName, new SoftReference<>(data));
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
