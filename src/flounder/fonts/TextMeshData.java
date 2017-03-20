package flounder.fonts;

/**
 * Stores the vertex data for all the quads on which a text will be rendered.
 */
public class TextMeshData {
	protected final float[] vertexPositions;
	protected final float[] textureCoords;

	protected TextMeshData(float[] vertexPositions, float[] textureCoords) {
		this.vertexPositions = vertexPositions;
		this.textureCoords = textureCoords;
	}
}
