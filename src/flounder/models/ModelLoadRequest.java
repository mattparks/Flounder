package flounder.models;

import flounder.processing.*;
import flounder.processing.glProcessing.*;

/**
 * A class that can process a request to load a model.
 */
public class ModelLoadRequest implements ResourceRequest, GlRequest {
	private Model model;
	private ModelBuilder builder;
	private ModelData data;
	private boolean sendRequest;

	/**
	 * Creates a new model load request.
	 *
	 * @param model The model object to load into.
	 * @param builder The builder to load from.
	 * @param sendRequest If a GL request should be sent, if false call {@link #executeGlRequest()} immediacy after {@link #doResourceRequest()}.
	 */
	protected ModelLoadRequest(Model model, ModelBuilder builder, boolean sendRequest) {
		this.model = model;
		this.builder = builder;
		this.sendRequest = sendRequest;
	}

	@Override
	public void doResourceRequest() {
		if (builder.getFile() != null) {
			data = FlounderModels.loadOBJ(builder.getFile());
		}

		if (sendRequest) {
			FlounderProcessors.sendGLRequest(this);
		}
	}

	@Override
	public void executeGlRequest() {
		if (builder.getFile() != null) {
			model.setName(builder.getFile().getPath());
			FlounderModels.loadModelToOpenGL(model, data);
		} else {
			model.setName(builder.getLoadManual().getModelName());
			FlounderModels.loadModelToOpenGL(model, builder.getLoadManual());
		}
	}
}
