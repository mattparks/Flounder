package com.flounder.animation;

import com.flounder.maths.matrices.*;

import java.util.*;

/**
 * Represents a joint in a "skeleton".
 * It contains the index of the joint which determines where in the vertex shader uniform array the joint matrix for this joint is loaded up to.
 * It also contains the name of the bone, and a list of all the child joints.
 * <p>
 * The "animatedTransform" matrix is the joint transform.
 * This is the transform that gets loaded up to the vertex shader and is used to transform vertices.
 * It is a model-space transform that transforms the joint from it's bind (original position, no animation applied) position to it's current position in the current pose.
 * Changing this transform changes the position/rotation of the joint in the animated entity.
 * <p>
 * The two other matrices are transforms that are required to calculate the "animatedTransform" in the {@link Animator} class.
 * It also has the local bind transform which is the original (no pose/animation applied) transform of the joint relative to the parent joint (in bone-space).
 * <p>
 * The "localBindTransform" is the original (bind) transform of the joint relative to its parent (in bone-space).
 * The inverseBindTransform is that bind transform in model-space, but inversed.
 */
public class Joint {
	protected final int index;
	protected final String name;

	protected final List<Joint> children;

	private final Matrix4f localBindTransform;
	private Matrix4f animatedTransform;
	private Matrix4f inverseBindTransform;

	/**
	 * Creates a new joint.
	 *
	 * @param index The joint's index (ID).
	 * @param name The name of the joint. This is how the joint is named in the collada file, and so is used to identify which joint a joint transform in an animation keyframe refers to.
	 * @param bindLocalTransform The bone-space transform of the joint in the bind position.
	 */
	public Joint(int index, String name, Matrix4f bindLocalTransform) {
		this.index = index;
		this.name = name;

		this.children = new ArrayList<>();

		this.localBindTransform = bindLocalTransform;
		this.animatedTransform = new Matrix4f();
		this.inverseBindTransform = new Matrix4f();
	}

	public int getIndex() {
		return index;
	}

	public String getName() {
		return name;
	}

	public List<Joint> getChildren() {
		return children;
	}

	/**
	 * Adds a child joint to this joint. Used during the creation of the joint hierarchy. Joints can have multiple children,
	 * which is why they are stored in a list (e.g. a "hand" joint may have multiple "finger" children joints).
	 *
	 * @param child The new child joint of this joint.
	 */
	public void addChild(Joint child) {
		this.children.add(child);
	}

	/**
	 * Adds this joint to an array, they for each child calls the same method.
	 *
	 * @param joints The array to add this and children into.
	 */
	public void addSelfAndChildren(List<Joint> joints) {
		joints.add(this);

		for (Joint child : children) {
			child.addSelfAndChildren(joints);
		}
	}

	public Matrix4f getLocalBindTransform() {
		return localBindTransform;
	}

	/**
	 * The animated transform is the transform that gets loaded up to the shader and is used to deform the vertices of the "skin". It represents the
	 * transformation from the joint's bind position (in model-space) to the joint's desired animation pose (also in model-space).
	 * This matrix is calculated by taking the desired model-space transform of the joint and multiplying it by the inverse of the starting model-space transform of the joint.
	 *
	 * @return The transformation matrix of the joint which is used to deform associated vertices of the skin in the shaders.
	 */
	public Matrix4f getAnimatedTransform() {
		return animatedTransform;
	}

	public void setAnimationTransform(Matrix4f animationTransform) {
		this.animatedTransform = animationTransform;
	}

	/**
	 * This returns the inverted model-space bind transform.
	 * The bind transform is the original model-space transform of the joint (when no animation is applied).
	 * This returns the inverse of that, which is used to calculate the animated transform matrix which gets used to transform vertices in the shader.
	 *
	 * @return The inverse of the joint's bind transform (in model-space).
	 */
	public Matrix4f getInverseBindTransform() {
		return inverseBindTransform;
	}

	/**
	 * This is called during set-up, after the joints hierarchy has been created. This calculates the model-space bind transform of this joint like so:
	 * {@code bindTransform = parentBindTransform * localBindTransform}</br>
	 * where "bindTransform" is the model-space bind transform of this joint, "parentBindTransform" is the model-space bind transform of the parent joint,
	 * and "localBindTransform" is the bone-space bind transform of this joint. It the calculates and stores the inverse of this model-space bind transform,
	 * for use when calculating the final animation transform each frame. It then recursively calls the method for all of the children joints,
	 * so that they too calculate and store their inverse bind-pose transform.
	 *
	 * @param parentBindTransform The model-space bind transform of the parent joint.
	 */
	public void calculateInverseBindTransform(Matrix4f parentBindTransform) {
		Matrix4f bindTransform = Matrix4f.multiply(parentBindTransform, localBindTransform, null);
		Matrix4f.invert(bindTransform, inverseBindTransform);

		for (Joint child : children) {
			child.calculateInverseBindTransform(bindTransform);
		}
	}
}
