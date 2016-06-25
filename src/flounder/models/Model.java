package flounder.models;

import flounder.engine.*;
import flounder.resources.*;

/**
 * Class that represents a loaded model.
 */
public class Model {
	private float[] vertices;
	private float[] textureCoords;
	private float[] normals;
	private float[] tangents;
	private int[] indices;
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

	protected void loadData(float[] vertices, float[] textureCoords, float[] normals, float[] tangents, int[] indices) {
		this.vertices = vertices;
		this.textureCoords = textureCoords;
		this.normals = normals;
		this.tangents = tangents;
		this.indices = indices;
		loaded = true;
	}

	public float[] getVertices() {
		return vertices;
	}

	public float[] getTextures() {
		return textureCoords;
	}

	public float[] getNormals() {
		return normals;
	}

	public float[] getTangents() {
		return tangents;
	}

	public int[] getIndices() {
		return indices;
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
	 * Gets if the texture is loaded.
	 *
	 * @return If the texture is loaded.
	 */
	public boolean isLoaded() {
		return loaded;
	}

	/**
	 * Deletes the model from OpenGL memory.
	 */
	public void delete() {
		loaded = false;
		FlounderEngine.getProcessors().sendGLRequest(new ModelDeleteRequest(vaoID));
	}
}
