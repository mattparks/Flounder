package flounder.models;

import flounder.engine.*;
import flounder.processing.*;
import flounder.processing.glProcessing.*;

import static org.lwjgl.opengl.GL30.*;

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
		data = FlounderEngine.getModels().loadOBJ(builder.getFile());

		if (sendRequest) {
			FlounderEngine.getProcessors().sendGLRequest(this);
		}
	}

	@Override
	public void executeGlRequest() {
		model.loadData(data);
		model.setVaoID(FlounderEngine.getLoader().createVAO());
		FlounderEngine.getLoader().createIndicesVBO(model.getVaoID(), model.getIndices());
		FlounderEngine.getLoader().storeDataInVBO(model.getVaoID(), model.getVertices(), 0, 3);
		FlounderEngine.getLoader().storeDataInVBO(model.getVaoID(), model.getTextures(), 1, 2);
		FlounderEngine.getLoader().storeDataInVBO(model.getVaoID(), model.getNormals(), 2, 3);
		FlounderEngine.getLoader().storeDataInVBO(model.getVaoID(), model.getTangents(), 3, 3);
		glBindVertexArray(0);
		model.setVaoLength(model.getIndices().length);
	}
}
