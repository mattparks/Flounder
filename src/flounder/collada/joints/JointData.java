package flounder.collada.joints;

import flounder.maths.matrices.*;

import java.util.*;

public class JointData {
	public final int index;
	public final String nameId;
	public final Matrix4f bindLocalTransform;

	public final List<JointData> children;

	public JointData(int index, String nameId, Matrix4f bindLocalTransform) {
		this.index = index;
		this.nameId = nameId;
		this.bindLocalTransform = bindLocalTransform;

		this.children = new ArrayList<>();
	}

	public void addChild(JointData child) {
		children.add(child);
	}

	/**
	 * Adds this joint to an array, they for each child calls the same method.
	 *
	 * @param joints The array to add this and children into.
	 */
	public void addSelfAndChildren(List<JointData> joints) {
		joints.add(this);

		for (JointData child : children) {
			child.addSelfAndChildren(joints);
		}
	}
}
