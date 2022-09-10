package com.flounder.models;

import com.flounder.factory.*;
import com.flounder.physics.*;
import com.flounder.processing.*;
import com.flounder.resources.*;

/**
 * Class that represents a loaded model.
 */
public class ModelObject extends FactoryObject {
	private float[] vertices;
	private float[] textures;
	private float[] normals;
	private float[] tangents;
	private int[] indices;
	private boolean smoothShading;

	private String name;
	private MyFile file;

	private Collider collider;
	private QuickHull quickHull;

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

		this.collider = null;
		this.quickHull = new QuickHull();

		this.vaoID = -1;
		this.vaoLength = -1;
	}

	protected void loadData(float[] vertices, float[] textureCoords, float[] normals, float[] tangents, int[] indices, boolean smoothShading, Collider collider, String name, MyFile file) {
		this.vertices = vertices;
		this.textures = textureCoords;
		this.normals = normals;
		this.tangents = tangents;
		this.indices = indices;
		this.smoothShading = smoothShading;

		this.name = name;
		this.file = file;

		this.collider = collider;

		if (vertices != null) {
			this.quickHull.loadData(vertices);
		}

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

	public Collider getCollider() {
		return collider;
	}

	public QuickHull getQuickHull() {
		return quickHull;
	}

	public int getVaoID() {
		return vaoID;
	}

	public int getVaoLength() {
		return vaoLength;
	}

	@Override
	public boolean isLoaded() {
		return super.isLoaded() && vaoID != -1 && vaoLength != -1;
	}

	/**
	 * Deletes the model from OpenGL memory.
	 */
	public void delete() {
		if (isLoaded()) {
			setFullyLoaded(false);
			FlounderProcessors.get().sendRequest(new ModelDeleteRequest(this));

			this.vertices = null;
			this.textures = null;
			this.normals = null;
			this.tangents = null;
			this.indices = null;
		}
	}
}
