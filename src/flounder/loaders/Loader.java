package flounder.loaders;

import flounder.maths.*;
import org.lwjgl.*;
import org.lwjgl.opengl.*;

import java.nio.*;
import java.util.*;

/**
 * Contains a lot of methods for VAO and VBO data management, and also keeps track of all currently active VAOs and VBOs.
 */
public class Loader {
	private static Map<Integer, List<Integer>> vaoCache = new HashMap<>();

	public static int storeDataInVBO(int vaoID, float[] data, int attributeNumber, int coordSize) {
		if (data == null) {
			return 0;
		}

		int bufferObjectID = GL15.glGenBuffers();
		vaoCache.get(vaoID).add(bufferObjectID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferObjectID);
		FloatBuffer buffer = Loader.storeDataInBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber, coordSize, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		return bufferObjectID;
	}

	private static FloatBuffer storeDataInBuffer(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	/**
	 * Loads interleaved vertex data into a VBO which is stored in a newly created VAO (without index buffer).
	 *
	 * @param data The vertex data.
	 * @param lengths The length (number of floats) of each data element. E.g. Data for positions, normals and texture coords may have lengths of 3, 3, 2.
	 *
	 * @return The ID of the new VAO.
	 */
	public static int createInterleavedVAO(float[] data, int... lengths) {
		int vertexArrayID = createVAO();
		storeInterleavedDataInVAO(vertexArrayID, data, lengths);
		return vertexArrayID;
	}

	/**
	 * Creates an empty VAO.
	 *
	 * @return The ID of the VAO.
	 */
	public static int createVAO() {
		int vertexArrayID = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vertexArrayID);
		vaoCache.put(vertexArrayID, new ArrayList<>());
		return vertexArrayID;
	}

	/**
	 * Stores interleaved data into a VAO.
	 *
	 * @param vaoID The ID of the VAO.
	 * @param data The interleaved float data.
	 * @param lengths The lengths in floats of each of the data elements associated with any given vertex.
	 */
	public static void storeInterleavedDataInVAO(int vaoID, float[] data, int... lengths) {
		FloatBuffer interleavedData = storeDataInBuffer(data);
		int bufferObjectID = GL15.glGenBuffers();
		vaoCache.get(vaoID).add(bufferObjectID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferObjectID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, interleavedData, GL15.GL_STATIC_DRAW);

		int total = 0;

		for (int length : lengths) {
			total += length;
		}

		int vertexByteCount = ByteWork.FLOAT_LENGTH * total;
		total = 0;

		for (int i = 0; i < lengths.length; i++) {
			GL20.glVertexAttribPointer(i, lengths[i], GL11.GL_FLOAT, false, vertexByteCount, ByteWork.FLOAT_LENGTH * total);
			total += lengths[i];
		}

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}

	public static int createEmptyVBO(int floatCount) {
		int bufferObjectID = GL15.glGenBuffers();
		// vaoCache.get(0).add(bufferObjectID); // TODO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferObjectID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, floatCount * 4, GL15.GL_STREAM_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		return bufferObjectID;
	}

	public static void addInstancedAttribute(int vao, int vbo, int attribute, int dataSize, int instancedDataLength, int offset) {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL30.glBindVertexArray(vao);
		GL20.glVertexAttribPointer(attribute, dataSize, GL11.GL_FLOAT, false, instancedDataLength * 4, offset * 4);
		GL33.glVertexAttribDivisor(attribute, 1);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}

	public static void updateVBO(int vbo, float[] data, FloatBuffer buffer) { // refillVBOWithData
		buffer.clear();
		buffer.put(data);
		buffer.flip();

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer.capacity() * 4, GL15.GL_STREAM_DRAW);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	/**
	 * Creates a VAO, interleaves the data, and stores it in the VAO. No index buffer is associated with the VAO.
	 *
	 * @param vertexCount The number of vertices whose data is being stored.
	 * @param data The various sets of data (positions, normals, texture coords, etc.)
	 *
	 * @return The newly created VAO.
	 */
	public static int createInterleavedVAO(int vertexCount, float[]... data) {
		int vertexArrayID = createVAO();
		float[] interleavedData = Loader.interleaveFloatData(vertexCount, data);
		int[] lengths = new int[data.length];

		for (int i = 0; i < data.length; i++) {
			lengths[i] = data[i].length / vertexCount;
		}

		storeInterleavedDataInVAO(vertexArrayID, interleavedData, lengths);
		return vertexArrayID;
	}

	/**
	 * Interleaved multiple float arrays of data into one interleaved float array.
	 *
	 * @param count The number of data elements (not floats) in the arrays.
	 * @param data The arrays of un-interleaved data.
	 *
	 * @return The interleaved data.
	 */
	public static float[] interleaveFloatData(int count, float[]... data) {
		int totalSize = 0;
		int[] lengths = new int[data.length];

		for (int i = 0; i < data.length; i++) {
			int elementLength = data[i].length / count;
			lengths[i] = elementLength;
			totalSize += data[i].length;
		}

		float[] interleavedBuffer = new float[totalSize];
		int pointer = 0;

		for (int i = 0; i < count; i++) {
			for (int j = 0; j < data.length; j++) {
				int elementLength = lengths[j];

				for (int k = 0; k < elementLength; k++) {
					interleavedBuffer[pointer++] = data[j][i * elementLength + k];
				}
			}
		}

		return interleavedBuffer;
	}

	/**
	 * Creates an index buffer and binds it to a VAO.
	 *
	 * @param vaoID The ID of the VAO to which the index buffer should be bound.
	 * @param indices The array of indices to be stored in the index buffer.
	 *
	 * @return The ID of the index buffer VBO.
	 */
	public static int createIndicesVBO(int vaoID, int[] indices) {
		if (indices == null) {
			return 0;
		}

		IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.length);
		indicesBuffer.put(indices);
		indicesBuffer.flip();
		int indicesBufferId = GL15.glGenBuffers();
		vaoCache.get(vaoID).add(indicesBufferId);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBufferId);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
		return indicesBufferId;
	}

	/**
	 * Creates an empty VBO for storing interleaved data and links it with the attribute lists of a VAO.
	 *
	 * @param vaoID The ID of the VAO to which the VBO should be added.
	 * @param maxVertexCount The maximum number of vertices that would need to be stored in the VBO.
	 * @param startingAttribute The first available attribute list of the VAO.
	 * @param lengths The lengths in floats of each of the data elements associated with any given vertex.
	 *
	 * @return The ID of the newly created VBO.
	 */
	public static int createEmptyInterleavedVBO(int vaoID, int maxVertexCount, int startingAttribute, int... lengths) {
		int bufferObjectID = GL15.glGenBuffers();
		vaoCache.get(vaoID).add(bufferObjectID);

		int total = 0;

		for (int length : lengths) {
			total += length;
		}

		int vertexByteCount = ByteWork.FLOAT_LENGTH * total;
		int maxSize = vertexByteCount * maxVertexCount;
		GL30.glBindVertexArray(vaoID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferObjectID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, maxSize, GL15.GL_DYNAMIC_DRAW);

		total = 0;

		for (int i = 0; i < lengths.length; i++) {
			GL20.glVertexAttribPointer(i + startingAttribute, lengths[i], GL11.GL_FLOAT, false, vertexByteCount, ByteWork.FLOAT_LENGTH * total);
			total += lengths[i];
		}

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);

		return bufferObjectID;
	}

	/**
	 * Store float data into part of a VBO. Can be used for updating data in a VBO.
	 *
	 * @param vbo The ID of the VBO.
	 * @param buffer A float buffer that can be used to store the data in the VBO. Must be bigger than {@code data.length}.
	 * @param data The float data to be stored in the VBO.
	 * @param startIndex The starting index in terms of floats for where the data should be stored in the VBO.
	 */
	public static void storeDataInVBO(int vbo, FloatBuffer buffer, float[] data, int startIndex) {
		buffer.clear();
		buffer.put(data);
		buffer.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, startIndex * ByteWork.FLOAT_LENGTH, buffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	/**
	 * Refills an entire VBO with new data.
	 *
	 * @param vbo The ID of the VBO.
	 * @param buffer A float buffer big enough to contain the data.
	 * @param data The data to be stored in the VBO.
	 */
	public static void refillVBOWithData(int vbo, FloatBuffer buffer, float[] data) {
		buffer.clear();
		buffer.put(data);
		buffer.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data.length * ByteWork.FLOAT_LENGTH, GL15.GL_DYNAMIC_DRAW);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	/**
	 * Deletes a VAO from memory along with any associated VBOs.
	 *
	 * @param vao The vao to be deleted.
	 */
	public static void deleteVAOFromCache(int vao) {
		vaoCache.remove(vao).forEach(GL15::glDeleteBuffers);
		GL30.glDeleteVertexArrays(vao);
	}

	/**
	 * Deletes all the VBOs and VAOs that are currently stored.
	 */
	public static void dispose() {
		GL20.glDisableVertexAttribArray(0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);

		for (int vaoID : vaoCache.keySet()) {
			vaoCache.get(vaoID).forEach(GL15::glDeleteBuffers);
			GL30.glDeleteVertexArrays(vaoID);
		}

		vaoCache.clear();
	}
}
