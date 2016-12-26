package flounder.models;

import flounder.materials.*;
import flounder.physics.*;

public class MeshData {
	private float[] vertices;
	private float[] textureCoords;
	private float[] normals;
	private float[] tangents;
	private int[] indices;
	private Material[] materials;

	private AABB aabb;
	private QuickHull hull;

	protected MeshData(float[] vertices, float[] textureCoords, float[] normals, float[] tangents, int[] indices, Material[] materials, AABB aabb, QuickHull hull) {
		this.vertices = vertices;
		this.textureCoords = textureCoords;
		this.normals = normals;
		this.tangents = tangents;
		this.indices = indices;
		this.materials = materials;

		this.aabb = aabb;
		this.hull = hull;
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

	public Material[] getMaterials() {
		return materials;
	}

	public AABB getAABB() {
		return aabb;
	}

	public QuickHull getHull() {
		return hull;
	}
}
