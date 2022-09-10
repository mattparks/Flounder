package com.flounder.collada.animation;

import com.flounder.maths.matrices.*;

public class JointTransformData {
	private final String jointNameId;
	private final Matrix4f jointLocalTransform;

	protected JointTransformData(String jointNameId, Matrix4f jointLocalTransform) {
		this.jointNameId = jointNameId;
		this.jointLocalTransform = jointLocalTransform;
	}

	public String getJointNameId() {
		return jointNameId;
	}

	public Matrix4f getJointLocalTransform() {
		return jointLocalTransform;
	}
}
