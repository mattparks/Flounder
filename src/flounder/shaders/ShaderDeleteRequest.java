package flounder.shaders;

import flounder.processing.glProcessing.*;

import static org.lwjgl.opengl.GL20.*;

/**
 * A class that can process a request to delete a model.
 */
public class ShaderDeleteRequest implements GlRequest {
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
	public void executeGlRequest() {
		glUseProgram(0);
		glDeleteProgram(shaderID);
	}
}
