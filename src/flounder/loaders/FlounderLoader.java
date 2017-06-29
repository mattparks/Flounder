package flounder.loaders;

import flounder.framework.*;

import java.nio.*;

/**
 * A module used for loading and managing OpenGL VAO's and VBO's.
 */
public class FlounderLoader extends Module {
	/**
	 * Creates a new OpenGL loader class.
	 */
	public FlounderLoader() {
		super();
	}

	@Module.Instance
	public static FlounderLoader get() {
		return (FlounderLoader) Framework.get().getInstance(FlounderLoader.class);
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
	}

	@Handler.Function(Handler.FLAG_UPDATE_PRE)
	public void update() {
	}

	/**
	 * Creates an empty VAO.
	 *
	 * @return The ID of the VAO.
	 */
	@Module.MethodReplace
	public int createVAO() {
		return -1;
	}

	/**
	 * Loads interleaved vertex data into a VBO which is stored in a newly created VAO (without index buffer).
	 *
	 * @param data The vertex data.
	 * @param lengths The length (number of floats) of each data element. E.g. Data for positions, normals and texture coords may have lengths of 3, 3, 2.
	 *
	 * @return The ID of the new VAO.
	 */
	@Module.MethodReplace
	public int createInterleavedVAO(float[] data, int... lengths) {
		return -1;
	}

	/**
	 * Creates a VAO, interleaves the data, and stores it in the VAO. No index buffer is associated with the VAO.
	 *
	 * @param vertexCount The number of vertices whose data is being stored.
	 * @param data The various sets of data (positions, normals, texture coords, etc.)
	 *
	 * @return The newly created VAO.
	 */
	@Module.MethodReplace
	public int createInterleavedVAO(int vertexCount, float[]... data) {
		return -1;
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
	@Module.MethodReplace
	public int createInterleavedVAO(float[] interleavedData, int[] indices, int... lengths) {
		return -1;
	}

	/**
	 * Stores interleaved data into a VAO.
	 *
	 * @param vaoID The ID of the VAO.
	 * @param data The interleaved float data.
	 * @param lengths The lengths in floats of each of the data elements associated with any given vertex.
	 */
	@Module.MethodReplace
	public void storeInterleavedDataInVAO(int vaoID, float[] data, int... lengths) {
	}

	/**
	 * Deletes a VAO from memory along with any associated VBOs.
	 *
	 * @param vao The vao to be deleted.
	 */
	@Module.MethodReplace
	public void deleteVAOFromCache(int vao) {
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
	@Module.MethodReplace
	public int createVBO(int vaoID, int maxCount, int startingAttribute, boolean instanced, int... lengths) {
		return -1;
	}

	/**
	 * Creates a new VBO with no data.
	 *
	 * @param floatCount The number of floats to be allotted.
	 *
	 * @return The new buffer objects ID.
	 */
	@Module.MethodReplace
	public int createEmptyVBO(int floatCount) {
		return -1;
	}

	/**
	 * Creates an index buffer and binds it to a VAO.
	 *
	 * @param vaoID The ID of the VAO to which the index buffer should be bound.
	 * @param indices The array of indices to be stored in the index buffer.
	 *
	 * @return The ID of the index buffer VBO.
	 */
	@Module.MethodReplace
	public int createIndicesVBO(int vaoID, int[] indices) {
		return -1;
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
	@Module.MethodReplace
	public int createInterleavedInstancedVBO(int vaoID, int maxVertexCount, int startingAttribute, int... lengths) {
		return -1;
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
	@Module.MethodReplace
	public int createEmptyInterleavedVBO(int vaoID, int maxInstanceCount, int startingAttribute, int... lengths) {
		return -1;
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
	@Module.MethodReplace
	public void addInstancedAttribute(int vao, int vbo, int attribute, int dataSize, int instancedDataLength, int offset) {
	}

	/**
	 * Store float data into part of a VBO. Can be used for updating data in a VBO.
	 *
	 * @param vbo The ID of the VBO.
	 * @param buffer A float buffer that can be used to store the data in the VBO. Must be bigger than {@code data.length}.
	 * @param data The float data to be stored in the VBO.
	 * @param startIndex The starting index in terms of floats for where the data should be stored in the VBO.
	 */
	@Module.MethodReplace
	public void storeDataInVBO(int vbo, FloatBuffer buffer, float[] data, int startIndex) {
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
	@Module.MethodReplace
	public int storeDataInVBO(int vaoID, float[] data, int attributeNumber, int coordSize) {
		return -1;
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
	@Module.MethodReplace
	public int storeDataInVBO(int vaoID, int[] data, int attributeNumber, int coordSize) {
		return -1;
	}

	/**
	 * Updates a FBO with a new set of data.
	 *
	 * @param vbo The FBO to update.
	 * @param data The data to add into the FBO.
	 * @param buffer A buffer to use to store the data in.
	 */
	@Module.MethodReplace
	public void updateVBO(int vbo, float[] data, FloatBuffer buffer) {
	}

	/**
	 * Refills an entire VBO with new data.
	 *
	 * @param vbo The ID of the VBO.
	 * @param buffer A float buffer big enough to contain the data.
	 * @param data The data to be stored in the VBO.
	 */
	@Module.MethodReplace
	public void refillVBOWithData(int vbo, FloatBuffer buffer, float[] data) {
	}

	/**
	 * Interleaved multiple float arrays of data into one interleaved float array.
	 *
	 * @param count The number of data elements (not floats) in the arrays.
	 * @param data The arrays of un-interleaved data.
	 *
	 * @return The interleaved data.
	 */
	@Module.MethodReplace
	public float[] interleaveFloatData(int count, float[]... data) {
		return null;
	}

	/**
	 * Stores a float array into a new float buffer.
	 *
	 * @param data The data to store.
	 *
	 * @return The data in a float buffer.
	 */
	@Module.MethodReplace
	public FloatBuffer storeDataInBuffer(float[] data) {
		return null;
	}

	/**
	 * Stores a int array into a new int buffer.
	 *
	 * @param data The data to store.
	 *
	 * @return The data in a int buffer.
	 */
	@Module.MethodReplace
	public IntBuffer storeDataInBuffer(int[] data) {
		return null;
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
	}
}
