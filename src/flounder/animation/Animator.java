package flounder.animation;

import flounder.framework.*;
import flounder.maths.matrices.*;

import java.util.*;

/**
 * This class contains all the functionality to apply an animation to an animated entity.
 * An Animator instance is associated with just one animated entity.
 * It also keeps track of the running time (in seconds) of the current animation,
 * along with a reference to the currently playing animation for the corresponding entity.
 * <p>
 * An Animator instance needs to be updated every frame, in order for it to keep updating the animation pose of the associated entity.
 * The currently playing animation can be changed at any time using the doAnimation() method.
 * The Animator will keep looping the current animation until a new animation is chosen.
 * <p>
 * The Animator calculates the desired current animation pose by interpolating between the previous and next keyframes of the animation
 * (based on the current animation time). The Animator then updates the transforms all of the joints each frame to match the current desired animation pose.
 */
public class Animator {
	private final Joint rootJoint;

	private float animationTime;
	private Animation currentAnimation;
	private Matrix4f animatorTransformation;

	/**
	 * Creates a new animator.
	 *
	 * @param rootJoint The root joint of the joint hierarchy which makes up the "skeleton" of the entity.
	 */
	public Animator(Joint rootJoint) {
		this.rootJoint = rootJoint;

		this.animationTime = 0;
		this.currentAnimation = null;
		this.animatorTransformation = new Matrix4f();
	}

	/**
	 * This method should be called each frame to update the animation currently being played. This increases the animation time (and loops it back to zero if necessary),
	 * finds the pose that the entity should be in at that time of the animation, and then applied that pose to all the entity's joints.
	 */
	public void update() {
		if (currentAnimation == null) {
			return;
		}

		increaseAnimationTime();
		Map<String, Matrix4f> currentPose = calculateCurrentAnimationPose();
		applyPoseToJoints(currentPose, rootJoint, animatorTransformation.setIdentity());
	}

	/**
	 * Increases the current animation time which allows the animation to progress. If the current animation has reached the end then the timer is reset, causing the animation to loop.
	 */
	private void increaseAnimationTime() {
		animationTime += Framework.getDelta();

		if (animationTime > currentAnimation.getLength()) {
			this.animationTime %= currentAnimation.getLength();
		}
	}

	/**
	 * This method returns the current animation pose of the entity. It returns
	 * the desired local-space transforms for all the joints in a map, indexed
	 * by the name of the joint that they correspond to.
	 * <p>
	 * The pose is calculated based on the previous and next keyframes in the
	 * current animation. Each keyframe provides the desired pose at a certain
	 * time in the animation, so the animated pose for the current time can be
	 * calculated by interpolating between the previous and next keyframe.
	 * <p>
	 * This method first finds the preious and next keyframe, calculates how far
	 * between the two the current animation is, and then calculated the pose
	 * for the current animation time by interpolating between the transforms at
	 * those keyframes.
	 *
	 * @return The current pose as a map of the desired local-space transforms for all the joints.
	 * The transforms are indexed by the name ID of the joint that they should be applied to.
	 */
	private Map<String, Matrix4f> calculateCurrentAnimationPose() {
		KeyFrames[] frames = getPreviousAndNextFrames();
		float progression = calculateProgression(frames[0], frames[1]);
		return interpolatePoses(frames[0], frames[1], progression);
	}

	/**
	 * Finds the previous keyframe in the animation and the next keyframe in the animation, and returns them in an array of length 2.
	 * If there is no previous frame (perhaps current animation time is 0.5 and the first keyframe is at time 1.5) then the first keyframe is used as both the previous and next keyframe.
	 * The last keyframe is used for both next and previous if there is no next keyframe.
	 *
	 * @return The previous and next keyframes, in an array which therefore will always have a length of 2.
	 */
	private KeyFrames[] getPreviousAndNextFrames() {
		KeyFrames[] allFrames = currentAnimation.getKeyFrames();
		KeyFrames previousFrame = allFrames[0];
		KeyFrames nextFrame = allFrames[0];

		for (int i = 1; i < allFrames.length; i++) {
			nextFrame = allFrames[i];

			if (nextFrame.getTimeStamp() > animationTime) {
				break;
			}

			previousFrame = allFrames[i];
		}

		return new KeyFrames[]{previousFrame, nextFrame};
	}

	/**
	 * Calculates how far between the previous and next keyframe the current animation time is, and returns it as a value between 0 and 1.
	 *
	 * @param previousFrame The previous keyframe in the animation.
	 * @param nextFrame The next keyframe in the animation.
	 *
	 * @return A number between 0 and 1 indicating how far between the two keyframes the current animation time is.
	 */
	private float calculateProgression(KeyFrames previousFrame, KeyFrames nextFrame) {
		float totalTime = nextFrame.getTimeStamp() - previousFrame.getTimeStamp();
		float currentTime = animationTime - previousFrame.getTimeStamp();
		return currentTime / totalTime;
	}

	/**
	 * Calculates all the local-space joint transforms for the desired current pose by interpolating between
	 * the transforms at the previous and next keyframes.
	 *
	 * @param previousFrame The previous keyframe in the animation.
	 * @param nextFrame The next keyframe in the animation.
	 * @param progression A number between 0 and 1 indicating how far between the previous and next keyframes the current animation time is.
	 *
	 * @return The local-space transforms for all the joints for the desired current pose.
	 * They are returned in a map, indexed by the name of the joint to which they should be applied.
	 */
	private Map<String, Matrix4f> interpolatePoses(KeyFrames previousFrame, KeyFrames nextFrame, float progression) {
		Map<String, Matrix4f> currentPose = new HashMap<>();

		for (String jointName : previousFrame.getPose().keySet()) {
			JointTransform previousTransform = previousFrame.getPose().get(jointName);
			JointTransform nextTransform = nextFrame.getPose().get(jointName);
			JointTransform currentTransform = JointTransform.interpolate(previousTransform, nextTransform, progression);
			currentPose.put(jointName, currentTransform.getLocalTransform());
		}

		return currentPose;
	}

	/**
	 * This method applies the current pose to a given joint, and all of its descendants.
	 * It does this by getting the desired local-transform for the
	 * current joint, before applying it to the joint. Before applying the
	 * transformations it needs to be converted from local-space to model-space
	 * (so that they are relative to the model's origin, rather than relative to
	 * the parent joint). This can be done by multiplying the local-transform of
	 * the joint with the model-space transform of the parent joint.
	 * <p>
	 * The same thing is then done to all the child joints.
	 * <p>
	 * Finally the inverse of the joint's bind transform is multiplied with the
	 * model-space transform of the joint. This basically "subtracts" the
	 * joint's original bind (no animation applied) transform from the desired
	 * pose transform. The result of this is then the transform required to verifyMove
	 * the joint from its original model-space transform to it's desired
	 * model-space posed transform. This is the transform that needs to be
	 * loaded up to the vertex shader and used to transform the vertices into
	 * the current pose.
	 *
	 * @param currentPose A map of the local-space transforms for all the joints for the desired pose. The map is indexed by the name of the joint which the transform corresponds to.
	 * @param joint The current joint which the pose should be applied to.
	 * @param parentTransform The desired model-space transform of the parent joint for the pose.
	 */
	private void applyPoseToJoints(Map<String, Matrix4f> currentPose, Joint joint, Matrix4f parentTransform) {
		Matrix4f currentLocalTransform = currentPose.get(joint.name);
		Matrix4f currentTransform = Matrix4f.multiply(parentTransform, currentLocalTransform, null);

		for (Joint childJoint : joint.children) {
			applyPoseToJoints(currentPose, childJoint, currentTransform);
		}

		Matrix4f.multiply(currentTransform, joint.getInverseBindTransform(), currentTransform);
		joint.setAnimationTransform(currentTransform);
	}

	/**
	 * Indicates that the entity should carry out the given animation. Resets the animation time so that the new animation starts from the beginning.
	 *
	 * @param animation The new animation to carry out.
	 */
	public void doAnimation(Animation animation) {
		this.animationTime = 0;
		this.currentAnimation = animation;
	}

	public Animation getCurrentAnimation() {
		return currentAnimation;
	}
}
