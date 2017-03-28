package flounder.animation;

import flounder.collada.*;
import flounder.collada.animation.*;
import flounder.framework.*;
import flounder.logger.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;
import flounder.processing.*;

import java.util.*;

/**
 * A module used for loading the information from a collada, and then creates an animation from the extracted data.
 */
public class FlounderAnimation extends Module {
	private static final FlounderAnimation INSTANCE = new FlounderAnimation();
	public static final String PROFILE_TAB_NAME = "Animation";

	/**
	 * Creates a new animation loader class.
	 */
	public FlounderAnimation() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME, FlounderLogger.class, FlounderProcessors.class, FlounderCollada.class);
	}

	@Override
	public void init() {
	}

	@Override
	public void update() {
	}

	@Override
	public void profile() {
	}

	/**
	 * Loads up a collada animation file, and returns and animation created from the extracted animation data from the file.
	 *
	 * @param animationData The animated data, loaded from a collada file.
	 *
	 * @return The animation made from the data in the file.
	 */
	public static Animation loadAnimation(AnimationData animationData) {
		KeyFrames[] frames = new KeyFrames[animationData.getKeyFrames().length];
		int pointer = 0;

		for (KeyFrameData frameData : animationData.getKeyFrames()) {
			frames[pointer++] = createKeyFrame(frameData);
		}

		return new Animation(animationData.getLengthSeconds(), frames);
	}

	private static KeyFrames createKeyFrame(KeyFrameData data) {
		Map<String, JointTransform> map = new HashMap<>();

		for (JointTransformData jointData : data.getJointTransforms()) {
			JointTransform jointTransform = createTransform(jointData);
			map.put(jointData.getJointNameId(), jointTransform);
		}

		return new KeyFrames(data.getTime(), map);
	}

	private static JointTransform createTransform(JointTransformData data) {
		Matrix4f matrix = data.getJointLocalTransform();
		Vector3f translation = new Vector3f(matrix.m30, matrix.m31, matrix.m32);
		Quaternion rotation = new Quaternion(matrix);
		return new JointTransform(translation, rotation);
	}

	@Override
	public Module getInstance() {
		return INSTANCE;
	}

	@Override
	public void dispose() {

	}
}
