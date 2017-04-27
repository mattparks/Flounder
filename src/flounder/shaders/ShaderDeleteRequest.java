package flounder.shaders;

import flounder.processing.opengl.*;

/**
 * A class that can process a request to delete a shader.
 */
public class ShaderDeleteRequest implements RequestOpenGL {
	private ShaderObject shader;

	/**
	 * Creates a new shader delete request.
	 *
	 * @param shader The OpenGL shader to be deleted.
	 */
	public ShaderDeleteRequest(ShaderObject shader) {
		this.shader = shader;
	}

	@Override
	public void executeRequestGL() {
		if (!FlounderShaders.getLoaded().containsKey(shader.getName())) {
			return;
		}

		FlounderShaders.getLoaded().get(shader.getName()).clear();
		FlounderShaders.getLoaded().remove(shader.getName());
		FlounderShaders.deleteShader(shader.getProgramID());
	}
}
