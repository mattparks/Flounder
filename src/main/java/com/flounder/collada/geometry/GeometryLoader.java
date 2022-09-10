package com.flounder.collada.geometry;

import com.flounder.collada.*;
import com.flounder.collada.skin.*;
import com.flounder.maths.matrices.*;
import com.flounder.maths.vectors.*;
import com.flounder.parsing.xml.*;
import com.flounder.physics.*;

import java.util.*;

/**
 * Loads the mesh data for a model from a collada XML file.
 */
public class GeometryLoader {
	private final XmlNode meshData;

	private final List<VertexSkinData> vertexWeights;

	private float[] verticesArray;
	private float[] texturesArray;
	private float[] normalsArray;
	private float[] tangentsArray;
	private int[] indicesArray;
	private int[] jointIdsArray;
	private float[] weightsArray;

	private List<VertexData> vertices;
	private List<Vector2f> textures;
	private List<Vector3f> normals;
	private List<Integer> indices;
	private AABB aabb;

	public GeometryLoader(XmlNode geometryNode, List<VertexSkinData> vertexWeights) {
		this.meshData = geometryNode.getChild("geometry").getChild("mesh");

		this.vertexWeights = vertexWeights;

		this.vertices = new ArrayList<>();
		this.textures = new ArrayList<>();
		this.normals = new ArrayList<>();
		this.indices = new ArrayList<>();
		this.aabb = new AABB();
	}

	public MeshData extractModelData() {
		readRawData();
		assembleVertices();
		removeUnusedVertices();
		initArrays();
		convertDataToArrays();
		convertIndicesListToArray();
		return new MeshData(verticesArray, texturesArray, normalsArray, tangentsArray, indicesArray, jointIdsArray, weightsArray, aabb);
	}

	private void readRawData() {
		readPositions();
		readNormals();
		readTextureCoords();
	}

	private void readPositions() {
		String positionsId = meshData.getChild("vertices").getChild("input").getAttribute("source").substring(1);
		XmlNode positionsData = meshData.getChildWithAttribute("source", "id", positionsId).getChild("float_array");
		int count = Integer.parseInt(positionsData.getAttribute("count"));
		String[] posData = positionsData.getData().split(" ");

		for (int i = 0; i < count / 3; i++) {
			float x = Float.parseFloat(posData[i * 3]);
			float y = Float.parseFloat(posData[i * 3 + 1]);
			float z = Float.parseFloat(posData[i * 3 + 2]);
			Vector4f position = new Vector4f(x, y, z, 1.0f);
			Matrix4f.transform(FlounderCollada.CORRECTION, position, position);
			expandAABB(position);
			VertexData vertexNew = new VertexData(vertices.size(), new Vector3f(position.x, position.y, position.z));
			vertexNew.setWeightsData(vertexWeights.get(vertices.size()));
			vertices.add(vertexNew);
		}
	}

	private void expandAABB(Vector4f newPosition) {
		if (newPosition.x < aabb.getMinExtents().x) {
			aabb.getMinExtents().x = newPosition.x;
		}

		if (newPosition.y < aabb.getMinExtents().y) {
			aabb.getMinExtents().y = newPosition.y;
		}

		if (newPosition.z < aabb.getMinExtents().z) {
			aabb.getMinExtents().z = newPosition.z;
		}

		if (newPosition.x > aabb.getMaxExtents().x) {
			aabb.getMaxExtents().x = newPosition.x;
		}

		if (newPosition.y > aabb.getMaxExtents().y) {
			aabb.getMaxExtents().y = newPosition.y;
		}

		if (newPosition.z > aabb.getMaxExtents().z) {
			aabb.getMaxExtents().z = newPosition.z;
		}
	}

	private void readNormals() {
		String normalsId = meshData.getChild("polylist").getChildWithAttribute("input", "semantic", "NORMAL").getAttribute("source").substring(1);
		XmlNode normalsData = meshData.getChildWithAttribute("source", "id", normalsId).getChild("float_array");
		int count = Integer.parseInt(normalsData.getAttribute("count"));
		String[] normData = normalsData.getData().split(" ");

		for (int i = 0; i < count / 3; i++) {
			float x = Float.parseFloat(normData[i * 3]);
			float y = Float.parseFloat(normData[i * 3 + 1]);
			float z = Float.parseFloat(normData[i * 3 + 2]);
			Vector4f normal = new Vector4f(x, y, z, 0.0f);
			Matrix4f.transform(FlounderCollada.CORRECTION, normal, normal);
			normals.add(new Vector3f(normal.x, normal.y, normal.z));
		}
	}

	private void readTextureCoords() {
		String texCoordsId = meshData.getChild("polylist").getChildWithAttribute("input", "semantic", "TEXCOORD").getAttribute("source").substring(1);
		XmlNode texCoordsData = meshData.getChildWithAttribute("source", "id", texCoordsId).getChild("float_array");
		int count = Integer.parseInt(texCoordsData.getAttribute("count"));
		String[] texData = texCoordsData.getData().split(" ");

		for (int i = 0; i < count / 2; i++) {
			float s = Float.parseFloat(texData[i * 2]);
			float t = Float.parseFloat(texData[i * 2 + 1]);
			textures.add(new Vector2f(s, t));
		}
	}

	private void assembleVertices() {
		XmlNode poly = meshData.getChild("polylist");
		int typeCount = poly.getChildren("input").size();
		String[] indexData = poly.getChild("p").getData().split(" ");

		for (int i = 0; i < indexData.length / typeCount; i++) {
			int positionIndex = Integer.parseInt(indexData[i * typeCount]);
			int normalIndex = Integer.parseInt(indexData[i * typeCount + 1]);
			int texCoordIndex = Integer.parseInt(indexData[i * typeCount + 2]);
			processVertex(positionIndex, normalIndex, texCoordIndex);
		}
	}

	private VertexData processVertex(int posIndex, int normIndex, int texIndex) {
		VertexData currentVertex = vertices.get(posIndex);

		if (!currentVertex.isSet()) {
			currentVertex.setTextureIndex(texIndex);
			currentVertex.setNormalIndex(normIndex);
			indices.add(posIndex);
			return currentVertex;
		} else {
			return dealWithAlreadyProcessedVertex(currentVertex, texIndex, normIndex);
		}
	}

	private VertexData dealWithAlreadyProcessedVertex(VertexData previousVertex, int newTextureIndex, int newNormalIndex) {
		if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
			indices.add(previousVertex.getIndex());
			return previousVertex;
		} else {
			VertexData anotherVertex = previousVertex.getDuplicateVertex();

			if (anotherVertex != null) {
				return dealWithAlreadyProcessedVertex(anotherVertex, newTextureIndex, newNormalIndex);
			} else {
				VertexData duplicateVertex = new VertexData(vertices.size(), previousVertex.getPosition());
				duplicateVertex.setTextureIndex(newTextureIndex);
				duplicateVertex.setNormalIndex(newNormalIndex);
				duplicateVertex.setWeightsData(previousVertex.getWeightsData());
				previousVertex.setDuplicateVertex(duplicateVertex);
				vertices.add(duplicateVertex);
				indices.add(duplicateVertex.getIndex());
				return duplicateVertex;
			}
		}
	}

	private void removeUnusedVertices() {
		for (VertexData vertex : vertices) {
			vertex.averageTangents();

			if (!vertex.isSet()) {
				vertex.setTextureIndex(0);
				vertex.setNormalIndex(0);
			}
		}
	}

	private void initArrays() {
		this.verticesArray = new float[vertices.size() * 3];
		this.texturesArray = new float[vertices.size() * 2];
		this.normalsArray = new float[vertices.size() * 3];
		this.tangentsArray = new float[vertices.size() * 3];
		this.jointIdsArray = new int[vertices.size() * 3];
		this.weightsArray = new float[vertices.size() * 3];
	}

	private float convertDataToArrays() {
		float furthestPoint = 0;

		for (int i = 0; i < vertices.size(); i++) {
			VertexData currentVertex = vertices.get(i);

			if (currentVertex.getLength() > furthestPoint) {
				furthestPoint = currentVertex.getLength();
			}

			Vector3f position = currentVertex.getPosition();
			Vector2f textureCoord = textures.get(currentVertex.getTextureIndex());
			Vector3f normalVector = normals.get(currentVertex.getNormalIndex());
			verticesArray[i * 3] = position.x;
			verticesArray[i * 3 + 1] = position.y;
			verticesArray[i * 3 + 2] = position.z;
			texturesArray[i * 2] = textureCoord.x;
			texturesArray[i * 2 + 1] = 1 - textureCoord.y;
			normalsArray[i * 3] = normalVector.x;
			normalsArray[i * 3 + 1] = normalVector.y;
			normalsArray[i * 3 + 2] = normalVector.z;
			VertexSkinData weights = currentVertex.getWeightsData();
			jointIdsArray[i * 3] = weights.getJointIds().get(0);
			jointIdsArray[i * 3 + 1] = weights.getJointIds().get(1);
			jointIdsArray[i * 3 + 2] = weights.getJointIds().get(2);
			weightsArray[i * 3] = weights.getWeights().get(0);
			weightsArray[i * 3 + 1] = weights.getWeights().get(1);
			weightsArray[i * 3 + 2] = weights.getWeights().get(2);
		}

		return furthestPoint;
	}

	private int[] convertIndicesListToArray() {
		this.indicesArray = new int[indices.size()];

		for (int i = 0; i < indicesArray.length; i++) {
			indicesArray[i] = indices.get(i);
		}

		return indicesArray;
	}
}
