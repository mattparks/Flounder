package flounder.models;

import flounder.processing.*;
import flounder.resources.*;

/**
 * Class that represents a loaded model.
 */
public class Model {
	private MeshData meshData;

	private String name;
	private boolean loaded;

	private int vaoID;
	private int vaoLength;

	/**
	 * Creates a new OpenGL model object.
	 */
	protected Model() {
		this.loaded = false;
	}

	/**
	 * Creates a new Model Builder.
	 *
	 * @param file The model file to be loaded.
	 *
	 * @return A new Model Builder.
	 */
	public static ModelBuilder newModel(MyFile file) {
		return new ModelBuilder(file);
	}

	/**
	 * Creates a new Model Builder.
	 *
	 * @param loadManual The model's manual loader.
	 *
	 * @return A new Model Builder.
	 */
	public static ModelBuilder newModel(ModelBuilder.LoadManual loadManual) {
		return new ModelBuilder(loadManual);
	}

	/**
	 * Creates a new empty Model.
	 *
	 * @return A new empty Model.
	 */
	public static Model getEmptyModel() {
		return new Model();
	}

	protected void loadData(ModelData data) {
		data.createRaw(this);
		data.destroy();
	}

	protected void loadData(MeshData meshData) {
		this.meshData = meshData;

		this.loaded = true;
	}

	public MeshData getMeshData() {
		return meshData;
	}

	public int getVaoID() {
		return vaoID;
	}

	public int getVaoLength() {
		return vaoLength;
	}

	public void setVaoID(int vaoID) {
		this.vaoID = vaoID;
	}

	public void setVaoLength(int vaoLength) {
		this.vaoLength = vaoLength;
	}

	/**
	 * Gets model name this was stored in.
	 *
	 * @return The model name.
	 */
	public String getFile() {
		return name;
	}

	/**
	 * Sets the name this model was loaded from.
	 *
	 * @param name The name this model was loaded from.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets if the model is loaded.
	 *
	 * @return If the model is loaded.
	 */
	public boolean isLoaded() {
		return loaded;
	}

	/**
	 * Deletes the model from OpenGL memory.
	 */
	public void delete() {
		loaded = false;
		FlounderProcessors.sendRequest(new ModelDeleteRequest(vaoID));
	}
}
