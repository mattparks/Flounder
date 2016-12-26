package flounder.models;

import flounder.framework.*;
import flounder.loaders.*;
import flounder.logger.*;
import flounder.materials.*;
import flounder.maths.vectors.*;
import flounder.processing.*;
import flounder.resources.*;

import java.io.*;
import java.lang.ref.*;
import java.util.*;

/**
 * A module used for loading and managing models.
 */
public class FlounderModels extends IModule {
	private static final FlounderModels instance = new FlounderModels();

	private Map<String, SoftReference<Model>> loaded;

	/**
	 * Creates a new model loader class.
	 */
	public FlounderModels() {
		super(ModuleUpdate.UPDATE_PRE, FlounderLogger.class, FlounderProcessors.class, FlounderLoader.class, FlounderMaterials.class);
	}

	@Override
	public void init() {
		this.loaded = new HashMap<>();
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
	public static ModelData loadOBJ(MyFile file) {
		BufferedReader reader;

		try {
			reader = file.getReader();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		ModelData modelData = new ModelData(file);

		Material currentMaterial = new Material();
		String line;

		try {
			while ((line = reader.readLine()) != null) {
				String prefix = line.split(" ")[0];

				if (prefix.equals("#")) {
					continue;
				}

				switch (prefix) {
					case "mtllib":
						String pathMTL = file.getPath().replace(file.getPath().split("/")[file.getPath().split("/").length - 1], "");
						pathMTL = pathMTL.substring(1, pathMTL.length() - 1);
						modelData.materials.addAll(FlounderMaterials.loadMTL(new MyFile(pathMTL, line.split(" ")[1])));
						break;
					case "usemtl":
						for (Material m : modelData.materials) {
							if (m.name.equals(line.split(" ")[1])) {
								currentMaterial = m;
							}
						}
						break;
					case "v":
						String[] currentLineV = line.split(" ");
						Vector3f vertex = new Vector3f(Float.valueOf(currentLineV[1]), Float.valueOf(currentLineV[2]), Float.valueOf(currentLineV[3]));
						VertexData newVertex = new VertexData(modelData.vertices.size(), vertex);
						modelData.vertices.add(newVertex);
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
						VertexData v0 = instance.processDataVertex(vertex1, modelData.vertices, modelData.indices, currentMaterial);
						VertexData v1 = instance.processDataVertex(vertex2, modelData.vertices, modelData.indices, currentMaterial);
						VertexData v2 = instance.processDataVertex(vertex3, modelData.vertices, modelData.indices, currentMaterial);
						instance.calculateTangents(v0, v1, v2, modelData.textures);
						break;
					default:
						FlounderLogger.warning("[OBJ " + file.getName() + "] Unknown Line: " + line);
						break;
				}
			}

			reader.close();
		} catch (IOException e) {
			FlounderLogger.error("Error reading the OBJ " + file);
		}

		return modelData;
	}

	/**
	 * Loads model data into the model data structure and OpenGL memory.
	 *
	 * @param model The model to be loaded to.
	 * @param data The data to be ued when loading to the model.
	 */
	public static void loadModelToOpenGL(Model model, ModelData data) {
		if (data != null) {
			model.loadData(data);
		}

		instance.loadModelToOpenGL(model);
	}

	/**
	 * Loads model data into the model data structure and OpenGL memory.
	 *
	 * @param model The model to be loaded to.
	 * @param loadManual The manual data to be ued when loading to the model.
	 */
	public static void loadModelToOpenGL(Model model, ModelBuilder.LoadManual loadManual) {
		if (loadManual != null) {
			MeshData meshData = new MeshData(loadManual.getVertices(), loadManual.getTextureCoords(), loadManual.getNormals(), loadManual.getTangents(), loadManual.getIndices(), loadManual.getMaterials(), loadManual.getAABB(), loadManual.getHull());
			model.loadData(meshData);
		}

		instance.loadModelToOpenGL(model);
	}

	private void loadModelToOpenGL(Model model) {
		model.setVaoID(FlounderLoader.createVAO());
		model.setVaoLength(model.getMeshData().getIndices() != null ? model.getMeshData().getIndices().length : (model.getMeshData().getVertices().length / 3));
		FlounderLoader.createIndicesVBO(model.getVaoID(), model.getMeshData().getIndices());
		FlounderLoader.storeDataInVBO(model.getVaoID(), model.getMeshData().getVertices(), 0, 3);
		FlounderLoader.storeDataInVBO(model.getVaoID(), model.getMeshData().getTextures(), 1, 2);
		FlounderLoader.storeDataInVBO(model.getVaoID(), model.getMeshData().getNormals(), 2, 3);
		FlounderLoader.storeDataInVBO(model.getVaoID(), model.getMeshData().getTangents(), 3, 3);
	}

	private VertexData processDataVertex(String[] vertex, List<VertexData> vertices, List<Integer> indices, Material currentMaterial) {
		int index = Integer.parseInt(vertex[0]) - 1;
		VertexData currentVertex = vertices.get(index);
		int textureIndex = Integer.parseInt(vertex[1]) - 1;
		int normalIndex = Integer.parseInt(vertex[2]) - 1;

		if (!currentVertex.isSet()) {
			currentVertex.setTextureIndex(textureIndex);
			currentVertex.setNormalIndex(normalIndex);
			currentVertex.setMaterial(currentMaterial);
			indices.add(index);
			return currentVertex;
		} else {
			return dealWithAlreadyProcessedDataVertex(currentVertex, textureIndex, normalIndex, indices, vertices, currentMaterial);
		}
	}

	private VertexData dealWithAlreadyProcessedDataVertex(VertexData previousVertex, int newTextureIndex, int newNormalIndex, List<Integer> indices, List<VertexData> vertices, Material currentMaterial) {
		if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
			indices.add(previousVertex.getIndex());
			return previousVertex;
		} else {
			VertexData anotherVertex = previousVertex.getDuplicateVertex();

			if (anotherVertex != null) {
				return dealWithAlreadyProcessedDataVertex(anotherVertex, newTextureIndex, newNormalIndex, indices, vertices, currentMaterial);
			} else {
				VertexData duplicateVertex = new VertexData(vertices.size(), previousVertex.getPosition());
				duplicateVertex.setTextureIndex(newTextureIndex);
				duplicateVertex.setNormalIndex(newNormalIndex);
				duplicateVertex.setMaterial(currentMaterial);
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

	/**
	 * Gets a list of loaded models.
	 *
	 * @return A list of loaded models.
	 */
	public static Map<String, SoftReference<Model>> getLoaded() {
		return instance.loaded;
	}

	@Override
	public IModule getInstance() {
		return instance;
	}

	@Override
	public void dispose() {
		loaded.keySet().forEach(key -> loaded.get(key).get().delete());
		loaded.clear();
	}
}
