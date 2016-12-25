package flounder.collada.geometry;

public class MeshData {
	private static final int DIMENSIONS = 3;

	private float[] vertices;
	private float[] textureCoords;
	private float[] normals;
	private int[] indices;
	private int[] jointIds;
	private float[] vertexWeights;
	private float furthestPoint;

	protected MeshData(float[] vertices, float[] textureCoords, float[] normals, int[] indices, int[] jointIds, float[] vertexWeights, float furthestPoint) {
		this.vertices = vertices;
		this.textureCoords = textureCoords;
		this.normals = normals;
		this.indices = indices;
		this.jointIds = jointIds;
		this.vertexWeights = vertexWeights;
		this.furthestPoint = furthestPoint;
	}

	public float[] getVertices() {
		return vertices;
	}

	public int getVertexCount() {
		return vertices.length / DIMENSIONS;
	}

	public float[] getTextures() {
		return textureCoords;
	}

	public float[] getNormals() {
		return normals;
	}

	public int[] getIndices() {
		return indices;
	}

	public int[] getJointIds() {
		return jointIds;
	}

	public float[] getVertexWeights() {
		return vertexWeights;
	}

	public float getFurthestPoint() {
		return furthestPoint;
	}
}
