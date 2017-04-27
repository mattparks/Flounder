package flounder.models;

import flounder.maths.vectors.*;

import java.util.*;

/**
 * A class that represents a single model vertex, used when loading OBJ files.
 */
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

	protected VertexData(int index, Vector3f position) {
		this.position = position;

		this.textureIndex = NO_INDEX;
		this.normalIndex = NO_INDEX;
		this.duplicateVertex = null;

		this.index = index;
		this.length = position.length();

		this.tangents = new ArrayList<>();
		this.averagedTangent = new Vector3f();
	}

	protected Vector3f getPosition() {
		return position;
	}

	protected int getTextureIndex() {
		return textureIndex;
	}

	protected void setTextureIndex(int textureIndex) {
		this.textureIndex = textureIndex;
	}

	protected int getNormalIndex() {
		return normalIndex;
	}

	protected void setNormalIndex(int normalIndex) {
		this.normalIndex = normalIndex;
	}

	protected boolean isSet() {
		return (textureIndex != NO_INDEX) && (normalIndex != NO_INDEX);
	}

	protected boolean hasSameTextureAndNormal(int textureIndexOther, int normalIndexOther) {
		return textureIndexOther == textureIndex && normalIndexOther == normalIndex;
	}

	protected VertexData getDuplicateVertex() {
		return duplicateVertex;
	}

	protected void setDuplicateVertex(VertexData duplicateVertex) {
		this.duplicateVertex = duplicateVertex;
	}

	protected int getIndex() {
		return index;
	}

	protected float getLength() {
		return length;
	}

	protected void addTangent(Vector3f tangent) {
		tangents.add(tangent);
	}

	protected void averageTangents() {
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

	protected Vector3f getAverageTangent() {
		return averagedTangent;
	}
}
