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
		if (!FlounderModels.get().getLoaded().containsKey(model.getName())) {
			return;
		}

		FlounderModels.get().getLoaded().get(model.getName()).clear();
		FlounderModels.get().getLoaded().remove(model.getName());

		FlounderLoader.get().deleteVAOFromCache(model.getVaoID());
	}
}
