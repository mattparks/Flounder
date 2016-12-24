package flounder.collada;

import java.util.*;

public class KeyFrameData {
	public final float time;
	public final List<JointTransformData> jointTransforms;

	protected KeyFrameData(float time) {
		this.time = time;
		this.jointTransforms = new ArrayList<>();
	}

	protected void addJointTransform(JointTransformData transform) {
		jointTransforms.add(transform);
	}
}
