package flounder.models;

import flounder.engine.*;
import flounder.maths.vectors.*;
import flounder.resources.*;

import java.io.*;
import java.util.*;

/**
 * Class capable of loading OBJ files into Models.
 */
public class FlounderModels implements IModule {
	public FlounderModels() {
	}

	@Override
	public void init() {
	}

	@Override
	public void update() {
	}

	@Override
	public void profile() {
	}

	/**
	 * Loads a OBJ file into a ModelRaw object.
	 *
	 * @param file The file to be loaded.
	 *
	 * @return The data loaded.
	 */
	public ModelData loadOBJ(MyFile file) {
		BufferedReader reader;

		try {
			reader = file.getReader();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		ModelData modelData = new ModelData(file);
		String line;

		try {
			while ((line = reader.readLine()) != null) {
				String prefix = line.split(" ")[0];

				if (prefix.equals("#")) {
					continue;
				}

				switch (prefix) {
					case "v":
						String[] currentLineV = line.split(" ");
						Vector3f vertex = new Vector3f(Float.valueOf(currentLineV[1]), Float.valueOf(currentLineV[2]), Float.valueOf(currentLineV[3]));
						VertexData newVertexData = new VertexData(modelData.vertices.size(), vertex);
						modelData.vertices.add(newVertexData);
						break;
					case "vt":
						String[] currentLineVT = line.split(" ");
						Vector2f texture = new Vector2f(Float.valueOf(currentLineVT[1]), Float.valueOf(currentLineVT[2]));
						modelData.textures.add(texture);
						break;
					case "vn":
						String[] currentLineVN = line.split(" ");
						Vector3f normal = new Vector3f(Float.valueOf(currentLineVN[1]), Float.valueOf(currentLineVN[2]), Float.valueOf(currentLineVN[3]));
						modelData.normals.add(normal);
						break;
					case "s":
						modelData.enableSmoothShading = !line.contains("off");
						break;
					case "f":
						String[] currentLineF = line.split(" ");
						String[] vertex1 = currentLineF[1].split("/");
						String[] vertex2 = currentLineF[2].split("/");
						String[] vertex3 = currentLineF[3].split("/");
						VertexData v0 = processDataVertex(vertex1, modelData.vertices, modelData.indices);
						VertexData v1 = processDataVertex(vertex2, modelData.vertices, modelData.indices);
						VertexData v2 = processDataVertex(vertex3, modelData.vertices, modelData.indices);
						calculateTangents(v0, v1, v2, modelData.textures);
						break;
					default:
						System.err.println("[OBJ " + file.getName() + "] Unknown Line: " + line);
						break;
				}
			}

			reader.close();
		} catch (IOException e) {
			FlounderEngine.getLogger().error("Error reading the OBJ " + file);
		}

		return modelData;
	}

	private static VertexData processDataVertex(String[] vertex, List<VertexData> vertices, List<Integer> indices) {
		int index = Integer.parseInt(vertex[0]) - 1;
		VertexData currentVertexData = vertices.get(index);
		int textureIndex = Integer.parseInt(vertex[1]) - 1;
		int normalIndex = Integer.parseInt(vertex[2]) - 1;

		if (!currentVertexData.isSet()) {
			currentVertexData.setTextureIndex(textureIndex);
			currentVertexData.setNormalIndex(normalIndex);
			indices.add(index);
			return currentVertexData;
		} else {
			return dealWithAlreadyProcessedDataVertex(currentVertexData, textureIndex, normalIndex, indices, vertices);
		}
	}

	private static VertexData dealWithAlreadyProcessedDataVertex(VertexData previousVertexData, int newTextureIndex, int newNormalIndex, List<Integer> indices, List<VertexData> vertices) {
		if (previousVertexData.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
			indices.add(previousVertexData.getIndex());
			return previousVertexData;
		} else {
			VertexData anotherVertexData = previousVertexData.getDuplicateVertex();

			if (anotherVertexData != null) {
				return dealWithAlreadyProcessedDataVertex(anotherVertexData, newTextureIndex, newNormalIndex, indices, vertices);
			} else {
				VertexData duplicateVertexData = new VertexData(vertices.size(), previousVertexData.getPosition());
				duplicateVertexData.setTextureIndex(newTextureIndex);
				duplicateVertexData.setNormalIndex(newNormalIndex);
				previousVertexData.setDuplicateVertex(duplicateVertexData);
				vertices.add(duplicateVertexData);
				indices.add(duplicateVertexData.getIndex());
				return duplicateVertexData;
			}
		}
	}

	private static void calculateTangents(VertexData v0, VertexData v1, VertexData v2, List<Vector2f> textures) {
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

	@Override
	public void dispose() {
	}
}
