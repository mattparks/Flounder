package flounder.animation;

import flounder.maths.matrices.*;
import flounder.maths.vectors.*;
import flounder.textures.*;

/**
 * @author Karl
 *         <p>
 *         This class represents an enity in the world that can be animated. It
 *         contains the model's VAO which contains the mesh data, the texture,
 *         and the root joint of the joint hierarchy, or "skeleton". It also
 *         holds an int which represents the number of joints that the model's
 *         skeleton contains, and has its own {@link Animator} instance which
 *         can be used to apply animations to this entity.
 */
public class AnimatedEntity {

	private final int modelVao;
	private final int indexCount;
	private final Texture texture;

	private final Joint rootJoint;
	private final int jointCount;

	private final Animator animator;

	/**
	 * Creates a new entity capable of animation. The inverse bind transform for
	 * all joints is calculated in this constructor.
	 *
	 * @param modelVao The VAO containing the mesh data for this entity. This
	 * includes vertex positions, normals, texture coords, IDs of
	 * joints that affect each vertex, and their corresponding
	 * weights.
	 * @param texture The diffuse texture for the entity.
	 * @param rootJoint The root joint of the joint hierarchy which makes up the
	 * "skeleton" of the entity.
	 * @param jointCount The number of joints in the joint hierarchy for this entity.
	 */
	public AnimatedEntity(int modelVao, int indexCount, Texture texture, Joint rootJoint, int jointCount) {
		this.modelVao = modelVao;
		this.indexCount = indexCount;
		this.texture = texture;
		this.rootJoint = rootJoint;
		this.jointCount = jointCount;
		this.animator = new Animator(this);
		rootJoint.calcInverseBindTransform(Matrix4f.rotate(new Matrix4f(), new Vector3f(1.0f, 0.0f, 0.0f), (float) Math.toRadians(-90.0f), null));
	}

	/**
	 * @return The VAO containing all the mesh data for this entity.
	 */
	public int getModel() {
		return modelVao;
	}

	public int getIndexCount() {
		return indexCount;
	}

	/**
	 * @return The diffuse texture for this entity.
	 */
	public Texture getTexture() {
		return texture;
	}

	/**
	 * @return The root joint of the joint hierarchy. This joint has no parent,
	 * and every other joint in the skeleton is a descendant of this
	 * joint.
	 */
	public Joint getRootJoint() {
		return rootJoint;
	}

	/**
	 * Deletes the OpenGL objects associated with this entity, namely the model
	 * (VAO) and texture.
	 */
	public void delete() {
		//	model.delete();
		texture.delete();
	}

	/**
	 * Instructs this entity to carry out a given animation.
	 *
	 * @param animation - the animation to be carried out.
	 */
	public void doAnimation(Animation animation) {
		animator.doAnimation(animation);
	}

	/**
	 * Updates the animator for this entity, basically updating the animated
	 * pose of the entity. Must be called every frame.
	 */
	public void update() {
		animator.update();
	}

	/**
	 * Gets an array of the model-space transforms of all the joints (with the
	 * current animation pose applied) in the entity. The joints are ordered in
	 * the array based on their joint index. The position of each joint's
	 * transform in the array is equal to the joint's index.
	 *
	 * @return The array of model-space transforms of the joints in the current
	 * animation pose.
	 */
	public Matrix4f[] getJointTransforms() {
		Matrix4f[] jointMatrices = new Matrix4f[jointCount];
		addJointsToArray(rootJoint, jointMatrices);
		return jointMatrices;
	}

	/**
	 * This adds the current model-space transform of a joint (and all of its
	 * descendants) into an array of transforms. The joint's transform is added
	 * into the array at the position equal to the joint's index.
	 *
	 * @param headJoint
	 * @param jointMatrices
	 */
	private void addJointsToArray(Joint headJoint, Matrix4f[] jointMatrices) {
		jointMatrices[headJoint.index] = headJoint.getAnimatedTransform();
		for (Joint childJoint : headJoint.children) {
			addJointsToArray(childJoint, jointMatrices);
		}
	}
}
