package flounder.models;

import flounder.materials.*;
import flounder.physics.*;

import java.util.*;

public class ModelData {
	private float[] vertices;
	private float[] textureCoords;
	private float[] normals;
	private float[] tangents;
	private int[] indices;
	private Material[] materials;
	private boolean enableSmoothShading;

	private String name;

	private AABB aabb;
	private QuickHull hull;

	public ModelData(float[] vertices, float[] textureCoords, float[] normals, float[] tangents, int[] indices, Material[] materials, AABB aabb, QuickHull hull, boolean enableSmoothShading, String name) {
		this.vertices = vertices;
		this.textureCoords = textureCoords;
		this.normals = normals;
		this.tangents = tangents;
		this.indices = indices;
		this.materials = materials;
		this.enableSmoothShading = enableSmoothShading;

		this.name = name;

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

	public String getName() {
		return name;
	}

	public AABB getAABB() {
		return aabb;
	}

	public QuickHull getHull() {
		return hull;
	}

	@Override
	public String toString() {
		return "ModelData{" +
				"vertices=" + Arrays.toString(vertices) +
				", textureCoords=" + Arrays.toString(textureCoords) +
				", normals=" + Arrays.toString(normals) +
				", tangents=" + Arrays.toString(tangents) +
				", indices=" + Arrays.toString(indices) +
				", materials=" + Arrays.toString(materials) +
				", enableSmoothShading=" + enableSmoothShading +
				", name=" + name +
				", aabb=" + aabb +
				", hull=" + hull +
				'}';
	}
}
