package flounder.models.animation;

import flounder.maths.matrices.*;

import java.util.*;

public class Joint {
	public int index;
	public String name;
	public List<Joint> children;

	private Matrix4f localBindTransformation;

	private Matrix4f inverseBindTransformation;
	private Matrix4f animationTransformation;

	public Joint(int index, String name, Matrix4f localBindTransformation) {
		this.index = index;
		this.name = name;
		this.children = new ArrayList<>();

		this.localBindTransformation = localBindTransformation;

		this.inverseBindTransformation = new Matrix4f();
		this.animationTransformation = new Matrix4f();
	}

	public void addChildren(Joint child) {
		children.add(child);
	}

	public Matrix4f getInverseBindTransformation() {
		return inverseBindTransformation;
	}

	public Matrix4f getAnimationTransformation() {
		return animationTransformation;
	}

	public void setAnimationTransformation(Matrix4f animationTransformation) {
		this.animationTransformation = animationTransformation;
	}

	protected void calculateInverseBindTransformation(Matrix4f parentBindTransformation) {
		Matrix4f bindTransformation = Matrix4f.multiply(parentBindTransformation, localBindTransformation, null);
		Matrix4f.invert(bindTransformation, inverseBindTransformation);

		for (Joint child: children) {
			child.calculateInverseBindTransformation(bindTransformation);
		}
	}
}
