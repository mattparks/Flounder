package flounder.collada;

import flounder.animation.*;
import flounder.collada.geometry.*;
import flounder.collada.joints.*;

public class AnimatedModelData {
	private final JointsData joints;
	private final Joint headJoint;
	private final MeshData mesh;

	protected AnimatedModelData(MeshData mesh, JointsData joints) {
		this.joints = joints;
		this.headJoint = createJoints(joints.headJoint);
		this.mesh = mesh;
	}

	private static Joint createJoints(JointData data) {
		Joint j = new Joint(data.index, data.nameId, data.bindLocalTransform);

		for (JointData child : data.children) {
			j.addChild(createJoints(child));
		}

		return j;
	}

	public Joint getHeadJoint() {
		return headJoint;
	}

	public JointsData getJointsData() {
		return joints;
	}

	public MeshData getMeshData() {
		return mesh;
	}
}
