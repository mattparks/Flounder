package flounder.models;

import flounder.processing.opengl.*;
import flounder.processing.resource.*;

/**
 * A class that can process a request to load a model.
 */
public class ModelLoadRequest implements RequestResource, RequestOpenGL {
	private Model model;
	private ModelBuilder builder;
	private ModelData data;

	/**
	 * Creates a new model load request.
	 *
	 * @param model The model object to load into.
	 * @param builder The builder to load from.
	 */
	protected ModelLoadRequest(Model model, ModelBuilder builder) {
		this.model = model;
		this.builder = builder;
	}

	@Override
	public void executeRequestResource() {
		if (builder.getFile() != null) {
			data = FlounderModels.loadOBJ(builder.getFile());
		}
	}

	@Override
	public void executeRequestGL() {
		if (builder.getFile() != null) {
			while (data == null) {
				// Wait for resources to load into data...
			}

			model.setName(builder.getFile().getPath());
			FlounderModels.loadModelToOpenGL(model, data);
		} else {
			model.setName(builder.getLoadManual().getModelName());
			FlounderModels.loadModelToOpenGL(model, builder.getLoadManual());
		}
	}
}