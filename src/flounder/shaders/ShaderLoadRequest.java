package flounder.shaders;

import flounder.engine.*;
import flounder.processing.*;
import flounder.processing.glProcessing.*;

/**
 * A class that can process a request to load a model.
 */
public class ShaderLoadRequest implements ResourceRequest, GlRequest {
	private Shader shader;
	private ShaderBuilder builder;
	private ShaderData data;
	private boolean sendRequest;

	/**
	 * Creates a new shader load request.
	 *
	 * @param shader The shader object to load into.
	 * @param builder The builder to load from.
	 * @param sendRequest If a GL request should be sent, if false call {@link #executeGlRequest()} immediacy after {@link #doResourceRequest()}.
	 */
	protected ShaderLoadRequest(Shader shader, ShaderBuilder builder, boolean sendRequest) {
		this.shader = shader;
		this.builder = builder;
		this.sendRequest = sendRequest;
	}

	@Override
	public void doResourceRequest() {
		data = FlounderEngine.getShaders().loadShader(builder);

		if (sendRequest) {
			FlounderEngine.getProcessors().sendGLRequest(this);
		}
	}

	@Override
	public void executeGlRequest() {
		FlounderEngine.getShaders().loadShaderToOpenGL(shader, data, builder);
	}
}
