package flounder.shaders;

import flounder.processing.opengl.*;

import static org.lwjgl.opengl.GL20.*;

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
		glUseProgram(0);
		glDeleteProgram(shader.getProgramID());
		FlounderShaders.getLoaded().remove(shader.getName());
	}
}
