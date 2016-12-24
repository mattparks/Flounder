package flounder.collada;

import flounder.maths.vectors.*;

import java.util.*;

public class Vertex {
	private static final int NO_INDEX = -1;

	private Vector3f position;

	private int textureIndex;
	private int normalIndex;
	private Vertex duplicateVertex;

	private int index;
	private float length;

	private List<Vector3f> tangents;
	private Vector3f averagedTangent;

	private VertexSkinData weightsData;

	public Vertex(int index, Vector3f position, VertexSkinData weightsData) {
		this.position = position;

		this.textureIndex = NO_INDEX;
		this.normalIndex = NO_INDEX;
		this.duplicateVertex = null;

		this.index = index;
		this.length = position.length();
		this.weightsData = weightsData;

		this.tangents = new ArrayList<>();
		this.averagedTangent = new Vector3f();
	}

	public VertexSkinData getWeightsData() {
		return weightsData;
	}

	public void addTangent(Vector3f tangent) {
		tangents.add(tangent);
	}

	public void averageTangents() {
		if (tangents.isEmpty()) {
			return;
		}

		for (Vector3f tangent : tangents) {
			Vector3f.add(averagedTangent, tangent, averagedTangent);
		}

		averagedTangent.normalize();
	}

	public Vector3f getAverageTangent() {
		return averagedTangent;
	}

	public int getIndex() {
		return index;
	}

	public float getLength() {
		return length;
	}

	public boolean isSet() {
		return textureIndex != NO_INDEX && normalIndex != NO_INDEX;
	}

	public boolean hasSameTextureAndNormal(int textureIndexOther, int normalIndexOther) {
		return textureIndexOther == textureIndex && normalIndexOther == normalIndex;
	}

	public void setTextureIndex(int textureIndex) {
		this.textureIndex = textureIndex;
	}

	public void setNormalIndex(int normalIndex) {
		this.normalIndex = normalIndex;
	}

	public Vector3f getPosition() {
		return position;
	}

	public int getTextureIndex() {
		return textureIndex;
	}

	public int getNormalIndex() {
		return normalIndex;
	}

	public Vertex getDuplicateVertex() {
		return duplicateVertex;
	}

	public void setDuplicateVertex(Vertex duplicateVertex) {
		this.duplicateVertex = duplicateVertex;
	}
}
