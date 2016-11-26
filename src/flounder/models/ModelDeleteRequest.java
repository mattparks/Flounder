package flounder.models;

import flounder.loaders.*;
import flounder.processing.opengl.*;

/**
 * A class that can process a request to delete a model.
 */
public class ModelDeleteRequest implements RequestOpenGL {
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
	public void executeRequestGL() {
		FlounderLoader.deleteVAOFromCache(modelID);
	}
}
