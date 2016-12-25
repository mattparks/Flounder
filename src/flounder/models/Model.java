package flounder.models;

import flounder.materials.*;
import flounder.maths.vectors.*;
import flounder.physics.*;
import flounder.processing.*;
import flounder.resources.*;

import java.util.*;

/**
 * Class that represents a loaded model.
 */
public class Model {
	private float[] vertices;
	private float[] textureCoords;
	private float[] normals;
	private float[] tangents;
	private int[] indices;
	private Material[] materials;

	private String name;
	private boolean loaded;

	private AABB aabb;
	private QuickHull hull;

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

	protected void loadData(float[] vertices, float[] textureCoords, float[] normals, float[] tangents, int[] indices, Material[] materials) {
		this.vertices = vertices;
		this.textureCoords = textureCoords;
		this.normals = normals;
		this.tangents = tangents;
		this.indices = indices;
		this.materials = materials;

		this.loaded = true;

		this.aabb = createAABB();
		this.hull = createHull();
	}

	private AABB createAABB() {
		float minX = 0, minY = 0, minZ = 0;
		float maxX = 0, maxY = 0, maxZ = 0;
		int tripleCount = 0;

		for (float position : vertices) {
			if (tripleCount == 0 && position < minX) {
				minX = position;
			} else if (tripleCount == 0 && position > maxX) {
				maxX = position;
			}

			if (tripleCount == 1 && position < minY) {
				minY = position;
			} else if (tripleCount == 1 && position > maxY) {
				maxY = position;
			}

			if (tripleCount == 2 && position < minZ) {
				minZ = position;
			} else if (tripleCount == 2 && position > maxZ) {
				maxZ = position;
			}

			if (tripleCount >= 2) {
				tripleCount = 0;
			} else {
				tripleCount++;
			}
		}

		return new AABB(new Vector3f(minX, minY, minZ), new Vector3f(maxX, maxY, maxZ));
	}

	private QuickHull createHull() {
		List<Vector3f> points = new ArrayList<>();

		for (int v = 0; v < vertices.length; v += 3) {
			points.add(new Vector3f(vertices[v], vertices[v + 1], vertices[v + 2]));
		}

		return new QuickHull(points);
	}

	public AABB getAABB() {
		return aabb;
	}

	public QuickHull getHull() {
		return hull;
	}

	public float[] getVertices() {
		return vertices;
	}

	public float[] getTextureCoords() {
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

	public Material[] getMaterials() {
		return materials;
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
