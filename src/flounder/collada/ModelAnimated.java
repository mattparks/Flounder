package flounder.collada;

import flounder.animation.*;
import flounder.collada.geometry.*;
import flounder.collada.joints.*;

public class ModelAnimated {
	private final MeshData meshData;

	private final JointsData jointsData;
	private final Joint headJoint;

	private int vaoID;
	private int vaoLength;

	public ModelAnimated(MeshData meshData, JointsData jointsData) {
		this.meshData = meshData;

		this.jointsData = jointsData;
		this.headJoint = createJoints(jointsData.getHeadJoint());

		FlounderCollada.loadModelToOpenGL(this);
	}

	private static Joint createJoints(JointData data) {
		Joint j = new Joint(data.getIndex(), data.getNameId(), data.getBindLocalTransform());

		for (JointData child : data.getChildren()) {
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

	public void setVaoID(int vaoID) {
		this.vaoID = vaoID;
	}

	public int getVaoLength() {
		return vaoLength;
	}

	public void setVaoLength(int vaoLength) {
		this.vaoLength = vaoLength;
	}

	/**
	 * Deletes the animated model from OpenGL memory.
	 */
	public void delete() {
		//	loaded = false;
		//	FlounderProcessors.sendRequest(new ModelDeleteRequest(vaoID));
	}
}
