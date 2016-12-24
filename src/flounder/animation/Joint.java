package flounder.animation;

import flounder.maths.matrices.*;

import java.util.*;

public class Joint {

	public final int index;
	public final String name;
	public final List<Joint> children = new ArrayList<Joint>();

	private final Matrix4f localBindTransform;

	private Matrix4f inverseBindTransform = new Matrix4f();
	private Matrix4f animatedTransform = new Matrix4f();

	public Joint(int index, String name, Matrix4f bindLocalTransform) {
		this.index = index;
		this.name = name;
		this.localBindTransform = bindLocalTransform;
	}

	/**
	 * Adds a child joint to this joint. Used during the creation of the joint
	 * hierarchy. Joints can have multiple children, which is why they are
	 * stored in a list (e.g. a "hand" joint may have multiple "finger" children
	 * joints).
	 *
	 * @param child - the new child joint of this joint.
	 */
	public void addChild(Joint child) {
		this.children.add(child);
	}

	/**
	 * This returns the inverted model-space bind transform. The bind transform
	 * is the original model-space transform of the joint (when no animation is
	 * applied). This returns the inverse of that, which is used to calculate
	 * the animated transform matrix which gets used to transform vertices in
	 * the shader.
	 *
	 * @return The inverse of the joint's bind transform (in model-space).
	 */
	public Matrix4f getInverseBindTransform() {
		return inverseBindTransform;
	}

	/**
	 * The animated transform is the transform that gets loaded up to the shader
	 * and is used to deform the vertices of the "skin". It represents the
	 * transformation from the joint's bind position (in model-space) to the
	 * joint's desired animation pose (also in model-space). This
	 * matrix is calculated by taking the desired model-space transform of the
	 * joint and multiplying it by the inverse of the starting model-space
	 * transform of the joint.
	 *
	 * @return The transformation matrix of the joint which is used to deform
	 * associated vertices of the skin in the shaders.
	 */
	public Matrix4f getAnimatedTransform() {
		return animatedTransform;
	}

	public void setAnimationTransform(Matrix4f animationTransform) {
		this.animatedTransform = animationTransform;
	}

	/**
	 * This is called during set-up, after the joints hierarchy has been
	 * created. This calculates the model-space bind transform of this joint
	 * like so: </br>
	 * </br>
	 * {@code bindTransform = parentBindTransform * localBindTransform}</br>
	 * </br>
	 * where "bindTransform" is the model-space bind transform of this joint,
	 * "parentBindTransform" is the model-space bind transform of the parent
	 * joint, and "localBindTransform" is the bone-space bind transform of this
	 * joint. It the calculates and stores the inverse of this model-space bind
	 * transform, for use when calculating the final animation transform each
	 * frame. It then recursively calls the method for all of the children
	 * joints, so that they too calculate and store their inverse bind-pose
	 * transform.
	 *
	 * @param parentBindTransform - the model-space bind transform of the parent joint.
	 */
	protected void calcInverseBindTransform(Matrix4f parentBindTransform) {
		Matrix4f bindTransform = Matrix4f.multiply(parentBindTransform, localBindTransform, null);
		Matrix4f.invert(bindTransform, inverseBindTransform);
		for (Joint child : children) {
			child.calcInverseBindTransform(bindTransform);
		}
	}

}
