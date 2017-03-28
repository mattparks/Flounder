package flounder.collada.joints;

import flounder.maths.matrices.*;

import java.util.*;

public class JointData {
	private final int index;
	private final String nameId;
	private final Matrix4f bindLocalTransform;

	private final Matrix4f inverseBindTransform;

	private final List<JointData> children;

	public JointData(int index, String nameId, Matrix4f bindLocalTransform, Matrix4f inverseBindTransform) {
		this.index = index;
		this.nameId = nameId;
		this.bindLocalTransform = bindLocalTransform;

		this.inverseBindTransform = inverseBindTransform;

		this.children = new ArrayList<>();
	}

	public int getIndex() {
		return index;
	}

	public String getNameId() {
		return nameId;
	}

	public Matrix4f getBindLocalTransform() {
		return bindLocalTransform;
	}

	public Matrix4f getInverseBindTransform() {
		return inverseBindTransform;
	}

	public List<JointData> getChildren() {
		return children;
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
