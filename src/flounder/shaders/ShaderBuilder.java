package flounder.shaders;

import flounder.engine.*;
import flounder.resources.*;

import java.lang.ref.*;
import java.util.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.*;

/**
 * A class capable of setting up a {@link flounder.shaders.Shader}.
 */
public class ShaderBuilder {
	private static Map<String, SoftReference<Shader>> loadedModels = new HashMap<>();

	private String shaderName;
	private MyFile fileVertex;
	private String stringVertex;
	private MyFile fileGeometry;
	private String stringGeometry;
	private MyFile fileFragment;
	private String stringFragment;

	/**
	 * Creates a class to setup a Shader.
	 *
	 * @param shaderName The name of the shader to be loaded.
	 */
	protected ShaderBuilder(String shaderName) {
		this.shaderName = shaderName;
	}

	/**
	 * Sets the source vertex shader file.
	 *
	 * @param fileVertex The source vertex shader file.
	 *
	 * @return this.
	 */
	public ShaderBuilder setVertex(MyFile fileVertex) {
		this.fileVertex = fileVertex;
		return this;
	}

	public ShaderBuilder setVertex(String stringVertex) {
		this.stringVertex = stringVertex;
		return this;
	}

	/**
	 * Sets the source geometry shader file.
	 *
	 * @param fileGeometry The source geometry shader file.
	 *
	 * @return this.
	 */
	public ShaderBuilder setGeometry(MyFile fileGeometry) {
		this.fileGeometry = fileGeometry;
		return this;
	}

	public ShaderBuilder setGeometry(String stringGeometry) {
		this.stringGeometry = stringGeometry;
		return this;
	}

	/**
	 * Sets the source fragment shader file.
	 *
	 * @param fileFragment The source fragment shader file.
	 *
	 * @return this.
	 */
	public ShaderBuilder setFragment(MyFile fileFragment) {
		this.fileFragment = fileFragment;
		return this;
	}

	public ShaderBuilder setFragment(String stringFragment) {
		this.stringFragment = stringFragment;
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
	 * Gets the source vertex shader file.
	 *
	 * @return The source vertex shader file.
	 */
	public MyFile getVertex() {
		return fileVertex;
	}

	public String getStringVertex() {
		return stringVertex;
	}

	/**
	 * Gets the source geometry shader file.
	 *
	 * @return The source geometry shader file.
	 */
	public MyFile getGeometry() {
		return fileGeometry;
	}

	public String getStringGeometry() {
		return stringGeometry;
	}

	/**
	 * Gets the source fragment shader file.
	 *
	 * @return The source fragment shader file.
	 */
	public MyFile getFragment() {
		return fileFragment;
	}

	public String getStringFragment() {
		return stringFragment;
	}

	public static class ShaderType {
		private int typeOpenGL;
		private Optional<MyFile> shaderFile;
		private Optional<String> shaderString;

		public ShaderType(int typeOpenGL, Optional<MyFile> shaderFile) {
			this.typeOpenGL = typeOpenGL;
			this.shaderFile = shaderFile;
		}
	}
}
