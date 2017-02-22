package flounder.models;

import flounder.loaders.*;
import flounder.processing.opengl.*;

/**
 * A class that can process a request to delete a model.
 */
public class ModelDeleteRequest implements RequestOpenGL {
	private ModelObject model;

	/**
	 * Creates a new model delete request.
	 *
	 * @param model The OpenGL model to be deleted.
	 */
	public ModelDeleteRequest(ModelObject model) {
		this.model = model;
	}

	@Override
	public void executeRequestGL() {
		FlounderLoader.deleteVAOFromCache(model.getVaoID());
		FlounderModels.getLoaded().remove(model.getName());
	}
}
