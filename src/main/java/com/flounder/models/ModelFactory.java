package com.flounder.models;

import com.flounder.factory.*;
import com.flounder.loaders.*;
import com.flounder.logger.*;
import com.flounder.maths.vectors.*;
import com.flounder.physics.*;
import com.flounder.resources.*;

import java.io.*;
import java.lang.ref.*;
import java.util.*;

/**
 * A class that represents a factory for loading models.
 */
public class ModelFactory extends Factory {
	private static final ModelFactory INSTANCE = new ModelFactory();

	private ModelFactory() {
		super("model");
	}

	/**
	 * Gets a new builder to be used to create information for build a object from.
	 *
	 * @return A new factory builder.
	 */
	public static ModelBuilder newBuilder() {
		return new ModelBuilder(INSTANCE);
	}

	@Override
	public ModelObject newObject() {
		return new ModelObject();
	}

	@Override
	public void loadData(FactoryObject object, FactoryBuilder builder, String name) {
		ModelBuilder b = (ModelBuilder) builder;
		ModelObject o = (ModelObject) object;

		if (((ModelBuilder) builder).getManual() != null) {
			ModelLoadManual m = b.getManual();
			o.loadData(m.getVertices(), m.getTextures(), m.getNormals(), m.getTangents(), m.getIndices(), m.isSmoothShading(), m.getAABB(), name, b.getFile());
		} else if (((ModelBuilder) builder).getFile() != null) {
			loadOBJ(o, b.getFile(), name);
		}
	}

	private void loadOBJ(ModelObject object, MyFile file, String name) {
		BufferedReader reader;

		try {
			reader = file.getReader();
		} catch (Exception e) {
			FlounderLogger.get().log(e);
			return;
		}

		List<Integer> indices = new ArrayList<>();
		List<VertexData> vertices = new ArrayList<>();
		List<Vector2f> textures = new ArrayList<>();
		List<Vector3f> normals = new ArrayList<>();
		boolean smoothShading = true;

		String line;

		if (reader == null) {
			FlounderLogger.get().error("Error creating reader the OBJ: " + file);
			return;
		}

		try {
			while ((line = reader.readLine()) != null) {
				String prefix = line.split(" ")[0];
				line = line.trim();

				if (prefix.equals("#")) {
					continue;
				}

				switch (prefix) {
					case "mtllib":
						break;
					case "usemtl":
						break;
					case "v":
						String[] currentLineV = line.split(" ");
						Vector3f vertex = new Vector3f(Float.valueOf(currentLineV[1]), Float.valueOf(currentLineV[2]), Float.valueOf(currentLineV[3]));
						VertexData newVertex = new VertexData(vertices.size(), vertex);
						vertices.add(newVertex);
						break;
					case "vt":
						String[] currentLineVT = line.split(" ");
						Vector2f texture = new Vector2f(Float.valueOf(currentLineVT[1]), Float.valueOf(currentLineVT[2]));
						textures.add(texture);
						break;
					case "vn":
						String[] currentLineVN = line.split(" ");
						Vector3f normal = new Vector3f(Float.valueOf(currentLineVN[1]), Float.valueOf(currentLineVN[2]), Float.valueOf(currentLineVN[3]));
						normals.add(normal);
						break;
					case "s":
						smoothShading = !line.contains("off");
						break;
					case "o":
						break;
					case "f":
						String[] currentLineF = line.split(" ");

						// The split length of 3 faced + 1 for the f prefix.
						if (currentLineF.length != 4 || line.contains("//")) {
							FlounderLogger.get().error("Error reading the OBJ " + file + ", it does not appear to be UV mapped! The model will not be loaded.");
							object.loadData(null, null, null, null, null, false, null, name, file);
							return;
						}

						String[] vertex1 = currentLineF[1].split("/");
						String[] vertex2 = currentLineF[2].split("/");
						String[] vertex3 = currentLineF[3].split("/");
						VertexData v0 = processDataVertex(vertex1, vertices, indices);
						VertexData v1 = processDataVertex(vertex2, vertices, indices);
						VertexData v2 = processDataVertex(vertex3, vertices, indices);
						calculateTangents(v0, v1, v2, textures);
						break;
					default:
						FlounderLogger.get().warning("[OBJ " + file.getName() + "] Unknown Line: " + line);
						break;
				}
			}

			reader.close();
		} catch (IOException e) {
			FlounderLogger.get().error("Error reading the OBJ " + file);
			FlounderLogger.get().exception(e);
		}

		// Averages out vertex tangents, and disabled non set vertices,
		for (VertexData vertex : vertices) {
			vertex.averageTangents();

			if (!vertex.isSet()) {
				vertex.setTextureIndex(0);
				vertex.setNormalIndex(0);
			}
		}

		// Turns the loaded OBJ data into a formal that can be used by OpenGL.
		int[] indicesArray = new int[indices.size()];
		float[] verticesArray = new float[vertices.size() * 3];
		float[] texturesArray = new float[vertices.size() * 2];
		float[] normalsArray = new float[vertices.size() * 3];
		float[] tangentsArray = new float[vertices.size() * 3];

		for (int i = 0; i < indicesArray.length; i++) {
			indicesArray[i] = indices.get(i);
		}

		for (int i = 0; i < vertices.size(); i++) {
			VertexData currentVertex = vertices.get(i);
			Vector3f position = currentVertex.getPosition();
			Vector2f textureCoord = textures.get(currentVertex.getTextureIndex());
			Vector3f normalVector = normals.get(currentVertex.getNormalIndex());
			Vector3f tangent = currentVertex.getAverageTangent();

			verticesArray[i * 3] = position.x;
			verticesArray[i * 3 + 1] = position.y;
			verticesArray[i * 3 + 2] = position.z;

			texturesArray[i * 2] = textureCoord.x;
			texturesArray[i * 2 + 1] = 1 - textureCoord.y;

			normalsArray[i * 3] = normalVector.x;
			normalsArray[i * 3 + 1] = normalVector.y;
			normalsArray[i * 3 + 2] = normalVector.z;

			tangentsArray[i * 3] = tangent.x;
			tangentsArray[i * 3 + 1] = tangent.y;
			tangentsArray[i * 3 + 2] = tangent.z;
		}

		/*FlounderLogger.get().log(name + ": indices={" + a_tsai(indicesArray) + "}, vertices={" + a_tsaf(verticesArray) + "}, textures={" + a_tsaf(texturesArray) + "}, normals={" + a_tsaf(normalsArray) + "}, tangents={" + a_tsaf(tangentsArray) + "};");
		private String a_tsaf(float[] array) {
			StringBuilder builder = new StringBuilder();
			for (float a : array)
				builder.append(a).append("f, ");
			return builder.toString();
		}
		private String a_tsai(int[] array) {
			StringBuilder builder = new StringBuilder();
			for (float a : array)
				builder.append(a).append(", ");
			return builder.toString();
		}*/

		// Takes OpenGL comparable data and loads it into a data object.
		/*AABB aabb = createAABB(vertices);
		aabb.getMinExtents().x = Math.abs(aabb.getMinExtents().x);
		aabb.getMinExtents().y = Math.abs(aabb.getMinExtents().y);
		aabb.getMinExtents().z = Math.abs(aabb.getMinExtents().z);
		aabb.getMaxExtents().x = Math.abs(aabb.getMaxExtents().x);
		aabb.getMaxExtents().y = Math.abs(aabb.getMaxExtents().y);
		aabb.getMaxExtents().z = Math.abs(aabb.getMaxExtents().z);
		float r = Math.max(Maths.max(aabb.getMinExtents()), Maths.max(aabb.getMaxExtents()));
		object.loadData(verticesArray, texturesArray, normalsArray, tangentsArray, indicesArray, smoothShading, new Sphere(r), name, file);*/
		object.loadData(verticesArray, texturesArray, normalsArray, tangentsArray, indicesArray, smoothShading, createAABB(vertices), name, file);
	}

	private VertexData processDataVertex(String[] vertex, List<VertexData> vertices, List<Integer> indices) {
		int index = Integer.parseInt(vertex[0]) - 1;
		VertexData currentVertex = vertices.get(index);
		int textureIndex = Integer.parseInt(vertex[1]) - 1;
		int normalIndex = Integer.parseInt(vertex[2]) - 1;

		if (!currentVertex.isSet()) {
			currentVertex.setTextureIndex(textureIndex);
			currentVertex.setNormalIndex(normalIndex);
			indices.add(index);
			return currentVertex;
		} else {
			return dealWithAlreadyProcessedDataVertex(currentVertex, textureIndex, normalIndex, indices, vertices);
		}
	}

	private VertexData dealWithAlreadyProcessedDataVertex(VertexData previousVertex, int newTextureIndex, int newNormalIndex, List<Integer> indices, List<VertexData> vertices) {
		if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
			indices.add(previousVertex.getIndex());
			return previousVertex;
		} else {
			VertexData anotherVertex = previousVertex.getDuplicateVertex();

			if (anotherVertex != null) {
				return dealWithAlreadyProcessedDataVertex(anotherVertex, newTextureIndex, newNormalIndex, indices, vertices);
			} else {
				VertexData duplicateVertex = new VertexData(vertices.size(), previousVertex.getPosition());
				duplicateVertex.setTextureIndex(newTextureIndex);
				duplicateVertex.setNormalIndex(newNormalIndex);
				previousVertex.setDuplicateVertex(duplicateVertex);
				vertices.add(duplicateVertex);
				indices.add(duplicateVertex.getIndex());
				return duplicateVertex;
			}
		}
	}

	private void calculateTangents(VertexData v0, VertexData v1, VertexData v2, List<Vector2f> textures) {
		Vector3f deltaPos1 = Vector3f.subtract(v1.getPosition(), v0.getPosition(), null);
		Vector3f deltaPos2 = Vector3f.subtract(v2.getPosition(), v0.getPosition(), null);
		Vector2f uv0 = textures.get(v0.getTextureIndex());
		Vector2f uv1 = textures.get(v1.getTextureIndex());
		Vector2f uv2 = textures.get(v2.getTextureIndex());
		Vector2f deltaUv1 = Vector2f.subtract(uv1, uv0, null);
		Vector2f deltaUv2 = Vector2f.subtract(uv2, uv0, null);

		float r = 1.0f / (deltaUv1.x * deltaUv2.y - deltaUv1.y * deltaUv2.x);
		deltaPos1.scale(deltaUv2.y);
		deltaPos2.scale(deltaUv1.y);
		Vector3f tangent = Vector3f.subtract(deltaPos1, deltaPos2, null);
		tangent.scale(r);
		v0.addTangent(tangent);
		v1.addTangent(tangent);
		v2.addTangent(tangent);
	}

	private AABB createAABB(List<VertexData> vertices) {
		float minX = Float.POSITIVE_INFINITY;
		float minY = Float.POSITIVE_INFINITY;
		float minZ = Float.POSITIVE_INFINITY;
		float maxX = Float.NEGATIVE_INFINITY;
		float maxY = Float.NEGATIVE_INFINITY;
		float maxZ = Float.NEGATIVE_INFINITY;

		for (VertexData vertex : vertices) {
			Vector3f vector = vertex.getPosition();

			if (vector.x < minX) {
				minX = vector.x;
			} else if (vector.x > maxX) {
				maxX = vector.x;
			}

			if (vector.y < minY) {
				minY = vector.y;
			} else if (vector.y > maxY) {
				maxY = vector.y;
			}

			if (vector.z < minZ) {
				minZ = vector.z;
			} else if (vector.z > maxZ) {
				maxZ = vector.z;
			}
		}

		return new AABB(new Vector3f(minX, minY, minZ), new Vector3f(maxX, maxY, maxZ));
	}

	@Override
	protected void create(FactoryObject object, FactoryBuilder builder) {
		// Takes OpenGL compatible data and loads it to the GPU and factory object.
		ModelBuilder b = (ModelBuilder) builder;
		ModelObject o = (ModelObject) object;

		if (o.getIndices() == null && o.getVertices() == null) {
			return;
		}

		int vaoID = FlounderLoader.get().createVAO();
		FlounderLoader.get().createIndicesVBO(vaoID, o.getIndices());
		FlounderLoader.get().storeDataInVBO(vaoID, o.getVertices(), 0, 3);
		FlounderLoader.get().storeDataInVBO(vaoID, o.getTextures(), 1, 2);
		FlounderLoader.get().storeDataInVBO(vaoID, o.getNormals(), 2, 3);
		FlounderLoader.get().storeDataInVBO(vaoID, o.getTangents(), 3, 3);
		int vaoLength = o.getIndices() != null ? o.getIndices().length : (o.getVertices().length / 3);
		((ModelObject) object).loadGL(vaoID, vaoLength);
	}

	@Override
	public Map<String, SoftReference<FactoryObject>> getLoaded() {
		return FlounderModels.get().getLoaded();
	}
}
