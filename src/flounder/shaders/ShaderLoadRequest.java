package flounder.shaders;

import flounder.processing.opengl.*;
import flounder.processing.resource.*;

/**
 * A class that can process a request to load a model.
 */
public class ShaderLoadRequest implements RequestResource, RequestOpenGL {
	private Shader shader;
	private ShaderBuilder builder;
	private ShaderData data;

	/**
	 * Creates a new shader load request.
	 *
	 * @param shader The shader object to load into.
	 * @param builder The builder to load from.
	 */
	protected ShaderLoadRequest(Shader shader, ShaderBuilder builder) {
		this.shader = shader;
		this.builder = builder;
	}

	@Override
	public void executeRequestResource() {
		data = FlounderShaders.loadShader(builder);
	}

	@Override
	public void executeRequestGL() {
		while (data == null) {
			// Wait for resources to load into data...
		}

		FlounderShaders.loadShaderToOpenGL(shader, data, builder);
	}
}