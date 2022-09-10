package com.flounder.loaders;

import com.flounder.framework.*;
import com.flounder.helpers.ByteWork;
import com.flounder.renderer.FlounderOpenGL;
import com.flounder.platform.FlounderPlatform;
import org.lwjgl.opengl.GL15;

import java.nio.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_INT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

/**
 * A module used for loading and managing OpenGL VAO's and VBO's.
 */
public class FlounderLoader extends com.flounder.framework.Module {
	private Map<Integer, List<Integer>> vaoCache;

	/**
	 * Creates a new OpenGL loader class.
	 */
	public FlounderLoader() {
		super();
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
		this.vaoCache = new HashMap<>();
	}

	@Handler.Function(Handler.FLAG_UPDATE_PRE)
	public void update() {
	}

	/**
	 * Creates an empty VAO.
	 *
	 * @return The ID of the VAO.
	 */
	public int createVAO() {
		int vertexArrayID = glGenVertexArrays();
		glBindVertexArray(vertexArrayID);
		this.vaoCache.put(vertexArrayID, new ArrayList<>());
		return vertexArrayID;
	}

	/**
	 * Loads interleaved vertex data into a VBO which is stored in a newly created VAO (without index buffer).
	 *
	 * @param data The vertex data.
	 * @param lengths The length (number of floats) of each data element. E.g. Data for positions, normals and texture coords may have lengths of 3, 3, 2.
	 *
	 * @return The ID of the new VAO.
	 */
	public int createInterleavedVAO(float[] data, int... lengths) {
		int vertexArrayID = createVAO();
		storeInterleavedDataInVAO(vertexArrayID, data, lengths);
		return vertexArrayID;
	}

	/**
	 * Creates a VAO, interleaves the data, and stores it in the VAO. No index buffer is associated with the VAO.
	 *
	 * @param vertexCount The number of vertices whose data is being stored.
	 * @param data The various sets of data (positions, normals, texture coords, etc.)
	 *
	 * @return The newly created VAO.
	 */
	public int createInterleavedVAO(int vertexCount, float[]... data) {
		int vertexArrayID = createVAO();
		float[] interleavedData = interleaveFloatData(vertexCount, data);
		int[] lengths = new int[data.length];

		for (int i = 0; i < data.length; i++) {
			lengths[i] = data[i].length / vertexCount;
		}

		storeInterleavedDataInVAO(vertexArrayID, interleavedData, lengths);
		return vertexArrayID;
	}

	/**
	 * Loads interleaved vertex data into a VBO which is stored in a newly created VAO (with index buffer).
	 *
	 * @param interleavedData VertexData data.
	 * @param indices Index buffer data.
	 * @param lengths The lengths of each data element.
	 *
	 * @return The ID of the new VAO.
	 */
	public int createInterleavedVAO(float[] interleavedData, int[] indices, int... lengths) {
		int vertexArrayID = createVAO();
		createIndicesVBO(vertexArrayID, indices);
		storeInterleavedDataInVAO(vertexArrayID, interleavedData, lengths);
		return vertexArrayID;
	}

	/**
	 * Stores interleaved data into a VAO.
	 *
	 * @param vaoID The ID of the VAO.
	 * @param data The interleaved float data.
	 * @param lengths The lengths in floats of each of the data elements associated with any given vertex.
	 */
	public void storeInterleavedDataInVAO(int vaoID, float[] data, int... lengths) {
		FloatBuffer interleavedData = this.storeDataInBuffer(data);
		int bufferObjectID = glGenBuffers();
		this.vaoCache.get(vaoID).add(bufferObjectID);
		glBindBuffer(GL_ARRAY_BUFFER, bufferObjectID);
		glBufferData(GL_ARRAY_BUFFER, interleavedData, GL_STATIC_DRAW);

		int total = 0;

		for (int length : lengths) {
			total += length;
		}

		int vertexByteCount = ByteWork.FLOAT_LENGTH * total;
		total = 0;

		for (int i = 0; i < lengths.length; i++) {
			glVertexAttribPointer(i, lengths[i], GL_FLOAT, false, vertexByteCount, ByteWork.FLOAT_LENGTH * total);
			total += lengths[i];
		}

		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}

	/**
	 * Deletes a VAO from memory along with any associated VBOs.
	 *
	 * @param vao The vao to be deleted.
	 */
	public void deleteVAOFromCache(int vao) {
		if (this.vaoCache.containsKey(vao)) {
			this.vaoCache.get(vao).forEach(GL15::glDeleteBuffers);
			this.vaoCache.get(vao).clear();
			this.vaoCache.remove(vao);
			glDeleteVertexArrays(vao);
		}
	}

	/**
	 * Creates an VBO for storing interleaved data and links it with the attribute lists of a VAO.
	 *
	 * @param vaoID The ID of the VAO to which the VBO should be added.
	 * @param maxCount The maximum number of values that would need to be stored in the VBO.
	 * @param startingAttribute The first available attribute list of the VAO.
	 * @param instanced If the VBO will be instanced.
	 * @param lengths The lengths in floats of each of the data elements associated with any given vertex.
	 *
	 * @return The ID of the newly created VBO.
	 */
	public int createVBO(int vaoID, int maxCount, int startingAttribute, boolean instanced, int... lengths) {
		int bufferObjectID = glGenBuffers();
		vaoCache.get(vaoID).add(bufferObjectID);

		int total = 0;

		for (int length : lengths) {
			total += length;
		}

		int vertexByteCount = ByteWork.FLOAT_LENGTH * total;
		int maxSize = vertexByteCount * maxCount;
		glBindVertexArray(vaoID);
		glBindBuffer(GL_ARRAY_BUFFER, bufferObjectID);
		glBufferData(GL_ARRAY_BUFFER, maxSize, GL_DYNAMIC_DRAW);

		total = 0;

		for (int i = 0; i < lengths.length; i++) {
			glVertexAttribPointer(i + startingAttribute, lengths[i], GL_FLOAT, false, vertexByteCount, ByteWork.FLOAT_LENGTH * total);

			if (instanced) {
				if (FlounderOpenGL.get().isModern()) {
					glVertexAttribDivisor(i + startingAttribute, 1); // TODO: Find non GL 3.3 version.
				}// else {
				//	ARBInstancedArrays.glVertexAttribDivisorARB(i + startingAttribute, 1);
				//}
			}

			total += lengths[i];
		}

		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
		return bufferObjectID;
	}

	/**
	 * Creates a new VBO with no data.
	 *
	 * @param floatCount The number of floats to be allotted.
	 *
	 * @return The new buffer objects ID.
	 */
	public int createEmptyVBO(int floatCount) {
		int bufferObjectID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, bufferObjectID);
		glBufferData(GL_ARRAY_BUFFER, floatCount * 4, GL_STREAM_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		return bufferObjectID;
	}

	/**
	 * Creates an index buffer and binds it to a VAO.
	 *
	 * @param vaoID The ID of the VAO to which the index buffer should be bound.
	 * @param indices The array of indices to be stored in the index buffer.
	 *
	 * @return The ID of the index buffer VBO.
	 */
	public int createIndicesVBO(int vaoID, int[] indices) {
		if (indices == null) {
			return 0;
		}

		IntBuffer indicesBuffer = FlounderPlatform.get().createIntBuffer(indices.length);
		indicesBuffer.put(indices);
		indicesBuffer.flip();
		int indicesBufferId = glGenBuffers();
		this.vaoCache.get(vaoID).add(indicesBufferId);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesBufferId);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
		return indicesBufferId;
	}

	/**
	 * Creates an VBO for storing interleaved data and links it with the attribute lists of a VAO.
	 *
	 * @param vaoID The ID of the VAO to which the VBO should be added.
	 * @param maxVertexCount The maximum number of vertices that would need to be stored in the VBO.
	 * @param startingAttribute The first available attribute list of the VAO.
	 * @param lengths The lengths in floats of each of the data elements associated with any given vertex.
	 *
	 * @return The ID of the newly created VBO.
	 */
	public int createInterleavedInstancedVBO(int vaoID, int maxVertexCount, int startingAttribute, int... lengths) {
		return createVBO(vaoID, maxVertexCount, startingAttribute, true, lengths);
	}

	/**
	 * Creates an empty VBO for storing interleaved data and links it with the attribute lists of a VAO.
	 *
	 * @param vaoID The ID of the VAO to which the VBO should be added.
	 * @param maxInstanceCount The maximum number of vertices that would need to be stored in the VBO.
	 * @param startingAttribute The first available attribute list of the VAO.
	 * @param lengths The lengths in floats of each of the data elements associated with any given vertex.
	 *
	 * @return The ID of the newly created VBO.
	 */
	public int createEmptyInterleavedVBO(int vaoID, int maxInstanceCount, int startingAttribute, int... lengths) {
		return createVBO(vaoID, maxInstanceCount, startingAttribute, false, lengths);
	}

	/**
	 * Adds a instances attribute to a VBO.
	 *
	 * @param vao The VBO's VAO.
	 * @param vbo The VBO.
	 * @param attribute The attribute to add data to.
	 * @param dataSize The size of data to add.
	 * @param instancedDataLength The length of data to allocate.
	 * @param offset The offset between data.
	 */
	public void addInstancedAttribute(int vao, int vbo, int attribute, int dataSize, int instancedDataLength, int offset) {
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBindVertexArray(vao);
		glVertexAttribPointer(attribute, dataSize, GL_FLOAT, false, instancedDataLength * 4, offset * 4);

		if (FlounderOpenGL.get().isModern()) {
			glVertexAttribDivisor(attribute, 1);
		}// else {
		//	ARBInstancedArrays.glVertexAttribDivisorARB(attribute, 1);
		//}

		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}

	/**
	 * Store float data into part of a VBO. Can be used for updating data in a VBO.
	 *
	 * @param vbo The ID of the VBO.
	 * @param buffer A float buffer that can be used to store the data in the VBO. Must be bigger than {@code data.length}.
	 * @param data The float data to be stored in the VBO.
	 * @param startIndex The starting index in terms of floats for where the data should be stored in the VBO.
	 */
	public void storeDataInVBO(int vbo, FloatBuffer buffer, float[] data, int startIndex) {
		buffer.clear();
		buffer.put(data);
		buffer.flip();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferSubData(GL_ARRAY_BUFFER, startIndex * ByteWork.FLOAT_LENGTH, buffer);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	/**
	 * Stores a float array of data into a FBO.
	 *
	 * @param vaoID The VAO to create a new FBO in.
	 * @param data The data to store.
	 * @param attributeNumber The attribute to create the FBO under.
	 * @param coordSize The size of data being store.
	 *
	 * @return The new FBO's ID.
	 */
	public int storeDataInVBO(int vaoID, float[] data, int attributeNumber, int coordSize) {
		if (data == null) {
			return 0;
		}

		int bufferObjectID = glGenBuffers();
		this.vaoCache.get(vaoID).add(bufferObjectID);
		glBindBuffer(GL_ARRAY_BUFFER, bufferObjectID);
		FloatBuffer buffer = this.storeDataInBuffer(data);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		glVertexAttribPointer(attributeNumber, coordSize, GL_FLOAT, false, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		return bufferObjectID;
	}

	/**
	 * Stores a integer array of data into a FBO.
	 *
	 * @param vaoID The VAO to create a new FBO in.
	 * @param data The data to store.
	 * @param attributeNumber The attribute to create the FBO under.
	 * @param coordSize The size of data being store.
	 *
	 * @return The new FBO's ID.
	 */
	public int storeDataInVBO(int vaoID, int[] data, int attributeNumber, int coordSize) {
		if (data == null) {
			return 0;
		}

		int bufferObjectID = glGenBuffers();
		this.vaoCache.get(vaoID).add(bufferObjectID);
		glBindBuffer(GL_ARRAY_BUFFER, bufferObjectID);
		IntBuffer buffer = this.storeDataInBuffer(data);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		glVertexAttribIPointer(attributeNumber, coordSize, GL_INT, coordSize * ByteWork.BYTES_PER_FLOAT, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		return bufferObjectID;
	}

	/**
	 * Updates a FBO with a new set of data.
	 *
	 * @param vbo The FBO to update.
	 * @param data The data to add into the FBO.
	 * @param buffer A buffer to use to store the data in.
	 */
	public void updateVBO(int vbo, float[] data, FloatBuffer buffer) {
		buffer.clear();
		buffer.put(data);
		buffer.flip();

		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, buffer.capacity() * 4, GL_STREAM_DRAW);
		glBufferSubData(GL_ARRAY_BUFFER, 0, buffer);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	/**
	 * Refills an entire VBO with new data.
	 *
	 * @param vbo The ID of the VBO.
	 * @param buffer A float buffer big enough to contain the data.
	 * @param data The data to be stored in the VBO.
	 */
	public void refillVBOWithData(int vbo, FloatBuffer buffer, float[] data) {
		buffer.clear();
		buffer.put(data);
		buffer.flip();

		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, data.length * ByteWork.FLOAT_LENGTH, GL_DYNAMIC_DRAW);
		glBufferSubData(GL_ARRAY_BUFFER, 0, buffer);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	/**
	 * Interleaved multiple float arrays of data into one interleaved float array.
	 *
	 * @param count The number of data elements (not floats) in the arrays.
	 * @param data The arrays of un-interleaved data.
	 *
	 * @return The interleaved data.
	 */
	public float[] interleaveFloatData(int count, float[]... data) {
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
	 * Stores a float array into a new float buffer.
	 *
	 * @param data The data to store.
	 *
	 * @return The data in a float buffer.
	 */
	public FloatBuffer storeDataInBuffer(float[] data) {
		FloatBuffer buffer = FlounderPlatform.get().createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	/**
	 * Stores a int array into a new int buffer.
	 *
	 * @param data The data to store.
	 *
	 * @return The data in a int buffer.
	 */
	public IntBuffer storeDataInBuffer(int[] data) {
		IntBuffer buffer = FlounderPlatform.get().createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
		glDisableVertexAttribArray(0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);

		for (int vaoID : vaoCache.keySet()) {
			vaoCache.get(vaoID).forEach(GL15::glDeleteBuffers);
			glDeleteVertexArrays(vaoID);
		}

		vaoCache.clear();
	}

	@com.flounder.framework.Module.Instance
	public static FlounderLoader get() {
		return (FlounderLoader) Framework.get().getModule(FlounderLoader.class);
	}
}
