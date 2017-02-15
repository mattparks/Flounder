package flounder.shaders;

import flounder.processing.opengl.*;

import static org.lwjgl.opengl.GL20.*;

/**
 * A class that can process a request to delete a shader.
 */
public class ShaderDeleteRequest implements RequestOpenGL {
	private int shaderID;

	/**
	 * Creates a new shader delete request.
	 *
	 * @param shaderID The OpenGL shader ID to be deleted.
	 */
	public ShaderDeleteRequest(int shaderID) {
		this.shaderID = shaderID;
	}

	@Override
	public void executeRequestGL() {
		glUseProgram(0);
		glDeleteProgram(shaderID);
	}
}
