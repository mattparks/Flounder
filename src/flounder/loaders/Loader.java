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
	private static final Map<Integer, List<Integer>> VAO_CACHE = new HashMap<>();

	/**
	 * Loads interleaved vertex data into a VBO which is stored in a newly created VAO (without index buffer).
	 *
	 * @param data Interleaved vertex data.
	 * @param lengths The length (number of floats) of each data element. E.g. Data for positions, normals and texture coords may have lengths of 3, 3, 2.
	 *
	 * @return The ID of the new VAO.
	 */
	public static int createInterleavedVAO(final float[] data, final int... lengths) {
		final int vertexArrayID = createVAO();
		storeInterleavedDataInVAO(vertexArrayID, data, lengths);
		return vertexArrayID;
	}

	/**
	 * Creates an empty VAO.
	 *
	 * @return The ID of the VAO.
	 */
	public static int createVAO() {
		final int vertexArrayID = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vertexArrayID);
		final List<Integer> associatedVbos = new ArrayList<>();
		VAO_CACHE.put(vertexArrayID, associatedVbos);
		return vertexArrayID;
	}

	/**
	 * Stores interleaved data into a VAO.
	 *
	 * @param vao The ID of the VAO.
	 * @param data The interleaved float data.
	 * @param lengths The lengths in floats of each of the data elements associated with any given vertex.
	 */
	public static void storeInterleavedDataInVAO(final int vao, final float[] data, final int... lengths) {
		FloatBuffer interleavedData = storeDataInBuffer(data);
		final int bufferObjectID = GL15.glGenBuffers();
		VAO_CACHE.get(vao).add(bufferObjectID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferObjectID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, interleavedData, GL15.GL_STATIC_DRAW);

		int total = 0;

		for (int i = 0; i < lengths.length; i++) {
			total += lengths[i];
		}

		final int vertexByteCount = ByteWork.FLOAT_LENGTH * total;
		total = 0;

		for (int i = 0; i < lengths.length; i++) {
			GL20.glVertexAttribPointer(i, lengths[i], GL11.GL_FLOAT, false, vertexByteCount, ByteWork.FLOAT_LENGTH * total);
			total += lengths[i];
		}

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}

	private static FloatBuffer storeDataInBuffer(final float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	/**
	 * Creates a VAO, interleaves the data, and stores it in the VAO. No index buffer is associated with the VAO.
	 *
	 * @param vertexCount The number of vertices whose data is being stored.
	 * @param data The various sets of data (positions, normals, texture coords, etc.)
	 *
	 * @return The newly created VAO.
	 */
	public static int createInterleavedVAO(final int vertexCount, final float[]... data) {
		final int vertexArrayID = createVAO();
		final float[] interleavedData = Loader.interleaveFloatData(vertexCount, data);
		final int[] lengths = new int[data.length];

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
	public static float[] interleaveFloatData(final int count, final float[]... data) {
		int totalSize = 0;
		final int[] lengths = new int[data.length];

		for (int i = 0; i < data.length; i++) {
			final int elementLength = data[i].length / count;
			lengths[i] = elementLength;
			totalSize += data[i].length;
		}

		final float[] interleavedBuffer = new float[totalSize];
		int pointer = 0;

		for (int i = 0; i < count; i++) {
			for (int j = 0; j < data.length; j++) {
				final int elementLength = lengths[j];

				for (int k = 0; k < elementLength; k++) {
					interleavedBuffer[pointer++] = data[j][i * elementLength + k];
				}
			}
		}

		return interleavedBuffer;
	}

	/**
	 * Loads interleaved vertex data into a VBO which is stored in a newly created VAO (with index buffer).
	 *
	 * @param data Interleaved vertex data.
	 * @param indices Index buffer data.
	 * @param lengths The lengths of each data element. E.g. Data for positions, normals and texture coords may have lengths of 3, 3, 2.
	 *
	 * @return The ID of the new VAO.
	 */
	public static int createInterleavedVAO(final float[] data, final int[] indices, final int... lengths) {
		final int vertexArrayID = createVAO();
		createIndicesVBO(vertexArrayID, indices);
		storeInterleavedDataInVAO(vertexArrayID, data, lengths);
		return vertexArrayID;
	}

	/**
	 * Creates an index buffer and binds it to a VAO.
	 *
	 * @param vao The ID of the VAO to which the index buffer should be bound.
	 * @param indices The array of indices to be stored in the index buffer.
	 *
	 * @return The ID of the index buffer VBO.
	 */
	public static int createIndicesVBO(final int vao, final int[] indices) {
		final IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.length);
		indicesBuffer.put(indices);
		indicesBuffer.flip();
		final int indicesBufferId = GL15.glGenBuffers();
		VAO_CACHE.get(vao).add(indicesBufferId);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBufferId);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
		return indicesBufferId;
	}

	public static int createInterleavedInstancedVbo(final int vao, final int maxVertexCount, final int startingAttribute, final int... lengths) {
		return createVBO(vao, maxVertexCount, startingAttribute, true, lengths);
	}

	private static int createVBO(final int vao, final int maxCount, final int startingAttribute, final boolean instanced, final int... lengths) {
		final int bufferObjectID = GL15.glGenBuffers();
		VAO_CACHE.get(vao).add(bufferObjectID);

		int total = 0;

		for (int i = 0; i < lengths.length; i++) {
			total += lengths[i];
		}

		final int vertexByteCount = ByteWork.FLOAT_LENGTH * total;
		final int maxSize = vertexByteCount * maxCount;
		GL30.glBindVertexArray(vao);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferObjectID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, maxSize, GL15.GL_DYNAMIC_DRAW);

		total = 0;

		for (int i = 0; i < lengths.length; i++) {
			GL20.glVertexAttribPointer(i + startingAttribute, lengths[i], GL11.GL_FLOAT, false, vertexByteCount, ByteWork.FLOAT_LENGTH * total);

			if (instanced) {
				GL33.glVertexAttribDivisor(i + startingAttribute, 1);
			}

			total += lengths[i];
		}

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
		return bufferObjectID;
	}

	/**
	 * Creates an empty VBO for storing interleaved data and links it with the attribute lists of a VAO.
	 *
	 * @param vao The ID of the VAO to which the VBO should be added.
	 * @param maxInstanceCount The maximum number of vertices that would need to be stored in the VBO.
	 * @param startingAttribute The first available attribute list of the VAO.
	 * @param lengths The lengths in floats of each of the data elements associated with any given vertex.
	 *
	 * @return The ID of the newly created VBO.
	 */
	public static int createEmptyInterleavedVBO(final int vao, final int maxInstanceCount, final int startingAttribute, final int... lengths) {
		return createVBO(vao, maxInstanceCount, startingAttribute, false, lengths);
	}

	/**
	 * Store float data into part of a VBO. Can be used for updating data in a VBO.
	 *
	 * @param vbo The ID of the VBO.
	 * @param buffer A float buffer that can be used to store the data in the VBO. Must be bigger than {@code data.length}.
	 * @param data The float data to be stored in the VBO.
	 * @param startIndex The starting index in terms of floats for where the data should be stored in the VBO.
	 */
	public static void storeDataInVBO(final int vbo, final FloatBuffer buffer, final float[] data, final int startIndex) {
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
	public static void refillVBOWithData(final int vbo, final FloatBuffer buffer, final float[] data) {
		buffer.clear();
		buffer.put(data);
		buffer.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer.capacity() * ByteWork.FLOAT_LENGTH, GL15.GL_DYNAMIC_DRAW);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	/**
	 * Deletes all the VBOs and VAOs that are currently stored.
	 */
	public static void dispose() {
		GL20.glDisableVertexAttribArray(0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);

		for (final int vaoID : VAO_CACHE.keySet()) {
			VAO_CACHE.get(vaoID).forEach(GL15::glDeleteBuffers);
			GL30.glDeleteVertexArrays(vaoID);
		}

		VAO_CACHE.clear();
	}

	/**
	 * Deletes a VAO from memory along with any associated VBOs.
	 *
	 * @param vao
	 */
	public static void deleteVAOFromCache(final int vao) {
		VAO_CACHE.remove(vao).forEach(GL15::glDeleteBuffers);
		GL30.glDeleteVertexArrays(vao);
	}
}
