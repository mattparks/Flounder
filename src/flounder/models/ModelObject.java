package flounder.models;

import flounder.factory.*;
import flounder.logger.*;
import flounder.physics.*;
import flounder.processing.*;
import flounder.resources.*;

/**
 * Class that represents a loaded model.
 */
public class ModelObject extends FactoryObject { // TODO: Document more!
	private float[] vertices;
	private float[] textures;
	private float[] normals;
	private float[] tangents;
	private int[] indices;
	private boolean smoothShading;

	private String name;
	private MyFile file;

	private AABB aabb;
	private QuickHull hull;

	private int vaoID;
	private int vaoLength;

	/**
	 * A new OpenGL model object.
	 */
	protected ModelObject() {
		super();
		this.vertices = null;
		this.textures = null;
		this.normals = null;
		this.tangents = null;
		this.indices = null;
		this.smoothShading = false;

		this.name = null;
		this.file = null;
	}

	protected void loadData(float[] vertices, float[] textureCoords, float[] normals, float[] tangents, int[] indices, boolean smoothShading, AABB aabb, QuickHull hull, String name, MyFile file) {
		this.vertices = vertices;
		this.textures = textureCoords;
		this.normals = normals;
		this.tangents = tangents;
		this.indices = indices;
		this.smoothShading = smoothShading;

		this.name = name;
		this.file = file;

		this.aabb = aabb;
		this.hull = hull;

		setDataLoaded(true);
	}

	protected void loadGL(int vaoID, int vaoLength) {
		this.vaoID = vaoID;
		this.vaoLength = vaoLength;

		setFullyLoaded(true);
	}

	public float[] getVertices() {
		return vertices;
	}

	public float[] getTextures() {
		return textures;
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

	public boolean isSmoothShading() {
		return smoothShading;
	}

	/**
	 * Gets the loaded name for the model.
	 *
	 * @return The models name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the model file this was stored in.
	 *
	 * @return The model file.
	 */
	public MyFile getFile() {
		return file;
	}

	public AABB getAABB() {
		return aabb;
	}

	public QuickHull getHull() {
		return hull;
	}

	public int getVaoID() {
		return vaoID;
	}

	public int getVaoLength() {
		return vaoLength;
	}

	/**
	 * Deletes the model from OpenGL memory.
	 */
	public void delete() {
		if (isLoaded()) {
			setFullyLoaded(false);
			FlounderProcessors.sendRequest(new ModelDeleteRequest(this));

			this.vertices = null;
			this.textures = null;
			this.normals = null;
			this.tangents = null;
			this.indices = null;
		}
	}
}
