package flounder.collada.skeleton;

import flounder.maths.matrices.*;

import java.util.*;

public class JointData {
	private final int index;
	private final String nameId;
	private final Matrix4f bindLocalTransform;

	private final List<JointData> children;

	public JointData(int index, String nameId, Matrix4f bindLocalTransform) {
		this.index = index;
		this.nameId = nameId;
		this.bindLocalTransform = bindLocalTransform;

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

	public List<JointData> getChildren() {
		return children;
	}

	public void addChild(JointData child) {
		children.add(child);
	}
}
