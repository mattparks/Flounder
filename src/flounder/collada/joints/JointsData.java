package flounder.collada.joints;

public class JointsData {
	private final int jointCount;
	private final JointData headJoint;

	public JointsData(int jointCount, JointData headJoint) {
		this.jointCount = jointCount;
		this.headJoint = headJoint;
	}

	public int getJointCount() {
		return jointCount;
	}

	public JointData getHeadJoint() {
		return headJoint;
	}
}
