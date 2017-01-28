package flounder.collada.animation;

import java.util.*;

public class AnimationKeyFrameData {
	private final float time;
	private final List<JointTransformData> jointTransforms;

	protected AnimationKeyFrameData(float time) {
		this.time = time;
		this.jointTransforms = new ArrayList<>();
	}

	public float getTime() {
		return time;
	}

	public List<JointTransformData> getJointTransforms() {
		return jointTransforms;
	}

	protected void addJointTransform(JointTransformData transform) {
		jointTransforms.add(transform);
	}
}
