package flounder.lwjgl3.loaders;

import flounder.framework.*;
import flounder.helpers.*;
import flounder.loaders.*;
import flounder.platform.*;

import java.nio.*;
import java.util.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL33.*;

@Module.ModuleOverride
public class LwjglLoaders extends FlounderLoader {
	private Map<Integer, List<Integer>> vaoCache;

	public LwjglLoaders() {
		super();
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
		this.vaoCache = new HashMap<>();

		super.init();
	}

	@Handler.Function(Handler.FLAG_UPDATE_PRE)
	public void update() {
		super.update();

	}

	@Override
	public int createVAO() {
		int vertexArrayID = glGenVertexArrays();
		glBindVertexArray(vertexArrayID);
		this.vaoCache.put(vertexArrayID, new ArrayList<>());
		return vertexArrayID;
	}

	@Override
	public int createInterleavedVAO(float[] data, int... lengths) {
		int vertexArrayID = createVAO();
		storeInterleavedDataInVAO(vertexArrayID, data, lengths);
		return vertexArrayID;
	}

	@Override
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

	@Override
	public int createInterleavedVAO(float[] interleavedData, int[] indices, int... lengths) {
		int vertexArrayID = createVAO();
		createIndicesVBO(vertexArrayID, indices);
		storeInterleavedDataInVAO(vertexArrayID, interleavedData, lengths);
		return vertexArrayID;
	}

	@Override
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

	@Override
	public void deleteVAOFromCache(int vao) {
		if (this.vaoCache.containsKey(vao)) {
			this.vaoCache.get(vao).forEach(key -> glDeleteBuffers(key));
			this.vaoCache.get(vao).clear();
			this.vaoCache.remove(vao);
			glDeleteVertexArrays(vao);
		}
	}

	@Override
	public int createVBO(int vaoID, int maxCount, int startingAttribute, boolean instanced, int... lengths) {
		int bufferObjectID = glGenBuffers();
		vaoCache.get(vaoID).add(bufferObjectID);

		int total = 0;

		for (int i = 0; i < lengths.length; i++) {
			total += lengths[i];
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

	@Override
	public int createEmptyVBO(int floatCount) {
		int bufferObjectID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, bufferObjectID);
		glBufferData(GL_ARRAY_BUFFER, floatCount * 4, GL_STREAM_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		return bufferObjectID;
	}

	@Override
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

	@Override
	public int createInterleavedInstancedVBO(int vaoID, int maxVertexCount, int startingAttribute, int... lengths) {
		return createVBO(vaoID, maxVertexCount, startingAttribute, true, lengths);
	}

	@Override
	public int createEmptyInterleavedVBO(int vaoID, int maxInstanceCount, int startingAttribute, int... lengths) {
		return createVBO(vaoID, maxInstanceCount, startingAttribute, false, lengths);
	}

	@Override
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

	@Override
	public void storeDataInVBO(int vbo, FloatBuffer buffer, float[] data, int startIndex) {
		buffer.clear();
		buffer.put(data);
		buffer.flip();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferSubData(GL_ARRAY_BUFFER, startIndex * ByteWork.FLOAT_LENGTH, buffer);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	@Override
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

	@Override
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

	@Override
	public void updateVBO(int vbo, float[] data, FloatBuffer buffer) {
		buffer.clear();
		buffer.put(data);
		buffer.flip();

		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, buffer.capacity() * 4, GL_STREAM_DRAW);
		glBufferSubData(GL_ARRAY_BUFFER, 0, buffer);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	@Override
	public void refillVBOWithData(int vbo, FloatBuffer buffer, float[] data) {
		buffer.clear();
		buffer.put(data);
		buffer.flip();

		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, data.length * ByteWork.FLOAT_LENGTH, GL_DYNAMIC_DRAW);
		glBufferSubData(GL_ARRAY_BUFFER, 0, buffer);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	@Override
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

	@Override
	public FloatBuffer storeDataInBuffer(float[] data) {
		FloatBuffer buffer = FlounderPlatform.get().createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	@Override
	public IntBuffer storeDataInBuffer(int[] data) {
		IntBuffer buffer = FlounderPlatform.get().createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
		super.dispose();
		glDisableVertexAttribArray(0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);

		for (int vaoID : vaoCache.keySet()) {
			vaoCache.get(vaoID).forEach(cache -> glDeleteBuffers(cache));
			glDeleteVertexArrays(vaoID);
		}

		vaoCache.clear();
	}
}
