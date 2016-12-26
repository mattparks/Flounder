package flounder.collada;

import flounder.animation.*;
import flounder.collada.geometry.*;
import flounder.collada.joints.*;
import flounder.loaders.*;

public class ModelAnimated {
	private final MeshData meshData;

	private final JointsData jointsData;
	private final Joint headJoint;

	private int vaoID;
	private int vaoLength;

	protected ModelAnimated(MeshData meshData, JointsData jointsData) {
		this.meshData = meshData;

		this.jointsData = jointsData;
		this.headJoint = createJoints(jointsData.headJoint);

		vaoID = FlounderLoader.createVAO();
		vaoLength = meshData.getIndices().length;
		FlounderLoader.createIndicesVBO(vaoID, meshData.getIndices());
		FlounderLoader.storeDataInVBO(vaoID, meshData.getVertices(), 0, 3);
		FlounderLoader.storeDataInVBO(vaoID, meshData.getTextures(), 1, 2);
		FlounderLoader.storeDataInVBO(vaoID, meshData.getNormals(), 2, 3);
		FlounderLoader.storeDataInVBO(vaoID, meshData.getTangents(), 3, 3);
		FlounderLoader.storeDataInVBO(vaoID, meshData.getJointIds(), 4, 3);
		FlounderLoader.storeDataInVBO(vaoID, meshData.getVertexWeights(), 5, 3);
	}

	private static Joint createJoints(JointData data) {
		Joint j = new Joint(data.index, data.nameId, data.bindLocalTransform);

		for (JointData child : data.children) {
			j.addChild(createJoints(child));
		}

		return j;
	}

	public MeshData getMeshData() {
		return meshData;
	}

	public JointsData getJointsData() {
		return jointsData;
	}

	public Joint getHeadJoint() {
		return headJoint;
	}

	public int getVaoID() {
		return vaoID;
	}

	public int getVaoLength() {
		return vaoLength;
	}

	/**
	 * Deletes the animated model from OpenGL memory.
	 */
	public void delete() {
		//	loaded = false;
		//	FlounderProcessors.sendRequest(new ModelDeleteRequest(vaoID));
	}
}
