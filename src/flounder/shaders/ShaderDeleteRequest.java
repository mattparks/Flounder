package flounder.shaders;

import flounder.engine.*;
import flounder.processing.glProcessing.*;

/**
 * A class that can process a request to delete a model.
 */
public class ShaderDeleteRequest implements GlRequest {
	private int modelID;

	/**
	 * Creates a new model delete request.
	 *
	 * @param modelID The OpenGL model VAO ID to be deleted.
	 */
	public ShaderDeleteRequest(int modelID) {
		this.modelID = modelID;
	}

	@Override
	public void executeGlRequest() {
		FlounderEngine.getLoader().deleteVAOFromCache(modelID);
	}
}
