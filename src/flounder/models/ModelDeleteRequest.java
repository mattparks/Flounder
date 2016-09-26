package flounder.models;

import flounder.loaders.*;
import flounder.processing.glProcessing.*;

/**
 * A class that can process a request to delete a model.
 */
public class ModelDeleteRequest implements GlRequest {
	private int modelID;

	/**
	 * Creates a new model delete request.
	 *
	 * @param modelID The OpenGL model VAO ID to be deleted.
	 */
	public ModelDeleteRequest(int modelID) {
		this.modelID = modelID;
	}

	@Override
	public void executeGlRequest() {
		FlounderLoader.deleteVAOFromCache(modelID);
	}
}
