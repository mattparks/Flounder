package com.flounder.collada.skeleton;

public class SkeletonData {
	private final int jointCount;
	private final JointData headJoint;

	public SkeletonData(int jointCount, JointData headJoint) {
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
