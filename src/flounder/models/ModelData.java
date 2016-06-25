package flounder.models;

import flounder.maths.vectors.*;
import flounder.resources.*;

import java.util.*;

public class ModelData {
	public MyFile file;
	public List<VertexData> vertices = new ArrayList<>();
	public List<Vector2f> textures = new ArrayList<>();
	public List<Vector3f> normals = new ArrayList<>();
	public List<Integer> indices = new ArrayList<>();
	public boolean enableSmoothShading = true;

	public ModelData(MyFile file) {
		this.file = file;
	}

	public void createRaw(Model model) {
		for (VertexData vertex : vertices) {
			if (!vertex.isSet()) {
				vertex.setTextureIndex(0);
				vertex.setNormalIndex(0);
			}
		}

		float[] verticesArray = new float[vertices.size() * 3];
		float[] texturesArray = new float[vertices.size() * 2];
		float[] normalsArray = new float[vertices.size() * 3];
		float[] tangentsArray = new float[vertices.size() * 3];

		for (int i = 0; i < vertices.size(); i++) {
			VertexData currentVertexData = vertices.get(i);
			Vector3f position = currentVertexData.getPosition();
			Vector2f textureCoord = textures.get(currentVertexData.getTextureIndex());
			Vector3f normalVector = normals.get(currentVertexData.getNormalIndex());
			Vector3f tangent = currentVertexData.getAverageTangent();

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

		model.loadData(verticesArray, texturesArray, normalsArray, tangentsArray, indicesArray);
	}

	public void destroy() {
		vertices = null;
		textures = null;
		normals = null;
		indices = null;
	}
}
