package flounder.models;

import flounder.materials.*;
import flounder.maths.vectors.*;

import java.util.*;

public class VertexData {
	private static final int NO_INDEX = -1;

	private Vector3f position;

	private int textureIndex;
	private int normalIndex;
	private VertexData duplicateVertex;

	private int index;
	private float length;

	private List<Vector3f> tangents;
	private Vector3f averagedTangent;

	private Material material;

	public VertexData(int index, Vector3f position) {
		this.position = position;

		this.textureIndex = NO_INDEX;
		this.normalIndex = NO_INDEX;
		this.duplicateVertex = null;

		this.index = index;
		this.length = position.length();

		this.tangents = new ArrayList<>();
		this.averagedTangent = new Vector3f();

		this.material = null;
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

	public boolean isSet() {
		return (textureIndex != NO_INDEX) && (normalIndex != NO_INDEX);
	}

	public boolean hasSameTextureAndNormal(int textureIndexOther, int normalIndexOther) {
		return textureIndexOther == textureIndex && normalIndexOther == normalIndex;
	}

	public VertexData getDuplicateVertex() {
		return duplicateVertex;
	}

	public void setDuplicateVertex(VertexData duplicateVertex) {
		this.duplicateVertex = duplicateVertex;
	}

	public int getIndex() {
		return index;
	}

	public float getLength() {
		return length;
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

		if (averagedTangent.length() > 0) {
			averagedTangent.normalize();
		}
	}

	public Vector3f getAverageTangent() {
		return averagedTangent;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}
}
