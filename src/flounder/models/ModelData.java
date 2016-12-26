package flounder.models;

import flounder.materials.*;
import flounder.maths.vectors.*;
import flounder.physics.*;
import flounder.resources.*;

import java.util.*;

public class ModelData {
	public MyFile file;

	public List<VertexData> vertices = new ArrayList<>();
	public List<Vector2f> textures = new ArrayList<>();
	public List<Vector3f> normals = new ArrayList<>();
	public List<Integer> indices = new ArrayList<>();
	public List<Material> materials = new ArrayList<>();
	public boolean enableSmoothShading = true;

	public ModelData(MyFile file) {
		this.file = file;
	}

	public void createRaw(Model model) {
		for (VertexData vertex : vertices) {
			vertex.averageTangents();

			if (!vertex.isSet()) {
				vertex.setTextureIndex(0);
				vertex.setNormalIndex(0);
			}
		}

		float[] verticesArray = new float[vertices.size() * 3];
		float[] texturesArray = new float[vertices.size() * 2];
		float[] normalsArray = new float[vertices.size() * 3];
		float[] tangentsArray = new float[vertices.size() * 3];
		Material[] materialsArray = new Material[vertices.size()];

		for (int i = 0; i < vertices.size(); i++) {
			VertexData currentVertex = vertices.get(i);
			Vector3f position = currentVertex.getPosition();
			Vector2f textureCoord = textures.get(currentVertex.getTextureIndex());
			Vector3f normalVector = normals.get(currentVertex.getNormalIndex());
			Vector3f tangent = currentVertex.getAverageTangent();
			Material material = currentVertex.getMaterial();

			materialsArray[i] = material;

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

		int[] indicesArray = new int[indices.size()];

		for (int i = 0; i < indicesArray.length; i++) {
			indicesArray[i] = indices.get(i);
		}

		MeshData modelData = new MeshData(verticesArray, texturesArray, normalsArray, tangentsArray, indicesArray, materialsArray, createAABB(), createHull());
		model.loadData(modelData);
	}

	private AABB createAABB() {
		float minX = 0, minY = 0, minZ = 0;
		float maxX = 0, maxY = 0, maxZ = 0;

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

	private QuickHull createHull() {
		List<Vector3f> points = new ArrayList<>();

		for (VertexData vertex : vertices) {
			points.add(vertex.getPosition());
		}

		return new QuickHull(points);
	}

	public void destroy() {
		vertices = null;
		textures = null;
		normals = null;
		indices = null;
		materials = null;
		enableSmoothShading = true;
	}
}
