package flounder.collada.joints;

public class JointsData {
	public final int jointCount;
	public final JointData headJoint;

	public JointsData(int jointCount, JointData headJoint) {
		this.jointCount = jointCount;
		this.headJoint = headJoint;
	}
}
