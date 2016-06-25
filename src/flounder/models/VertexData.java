package flounder.models;

import flounder.maths.vectors.*;

import java.util.*;

public class VertexData {
	public static final int NO_INDEX = -1;

	private Vector3f position;
	private int textureIndex;
	private int normalIndex;
	private int index;
	private float length;
	private List<Vector3f> tangents;
	private Vector3f averagedTangent;
	private VertexData duplicateVertexData;

	public VertexData(int index, Vector3f position) {
		this.index = index;
		this.position = position;
		textureIndex = NO_INDEX;
		normalIndex = NO_INDEX;
		length = position.length();
		tangents = new ArrayList<>();
		averagedTangent = new Vector3f(0, 0, 0);
		duplicateVertexData = null;
	}

	public int getIndex() {
		return index;
	}

	public float getLength() {
		return length;
	}

	public boolean isSet() {
		return (textureIndex != NO_INDEX) && (normalIndex != NO_INDEX);
	}

	public boolean hasSameTextureAndNormal(int textureIndexOther, int normalIndexOther) {
		return textureIndexOther == textureIndex && normalIndexOther == normalIndex;
	}

	public Vector3f getPosition() {
		return position;
	}

	public int getTextureIndex() {
		return textureIndex;
	}

	public void setTextureIndex(int textureIndex) {
		this.textureIndex = textureIndex;
	}

	public int getNormalIndex() {
		return normalIndex;
	}

	public void setNormalIndex(int normalIndex) {
		this.normalIndex = normalIndex;
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

	public VertexData getDuplicateVertex() {
		return duplicateVertexData;
	}

	public void setDuplicateVertex(VertexData duplicateVertexData) {
		this.duplicateVertexData = duplicateVertexData;
	}
}
