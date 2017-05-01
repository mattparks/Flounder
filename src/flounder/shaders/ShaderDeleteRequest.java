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
		if (!FlounderShaders.get().getLoaded().containsKey(shader.getName())) {
			return;
		}

		FlounderShaders.get().getLoaded().get(shader.getName()).clear();
		FlounderShaders.get().getLoaded().remove(shader.getName());
		FlounderShaders.get().deleteShader(shader.getProgramID());
	}
}
