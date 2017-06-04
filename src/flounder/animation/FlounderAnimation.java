package flounder.animation;

import flounder.collada.*;
import flounder.collada.animation.*;
import flounder.framework.*;
import flounder.loaders.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;
import flounder.processing.*;

import java.util.*;

/**
 * A module used for loading the information from a collada, and then creates an animation from the extracted data.
 */
public class FlounderAnimation extends Module {
	/**
	 * Creates a new animation loader class.
	 */
	public FlounderAnimation() {
		super(FlounderProcessors.class, FlounderLoader.class, FlounderCollada.class);
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
	}

	@Handler.Function(Handler.FLAG_UPDATE_PRE)
	public void update() {
	}

	/**
	 * Loads up a collada animation file, and returns and animation created from the extracted animation data from the file.
	 *
	 * @param animationData The animated data, loaded from a collada file.
	 *
	 * @return The animation made from the data in the file.
	 */
	public Animation loadAnimation(AnimationData animationData) {
		KeyFrames[] frames = new KeyFrames[animationData.getKeyFrames().length];
		int pointer = 0;

		for (KeyFrameData frameData : animationData.getKeyFrames()) {
			frames[pointer++] = createKeyFrame(frameData);
		}

		return new Animation(animationData.getLengthSeconds(), frames);
	}

	private KeyFrames createKeyFrame(KeyFrameData data) {
		Map<String, JointTransform> map = new HashMap<>();

		for (JointTransformData jointData : data.getJointTransforms()) {
			JointTransform jointTransform = createTransform(jointData);
			map.put(jointData.getJointNameId(), jointTransform);
		}

		return new KeyFrames(data.getTime(), map);
	}

	private JointTransform createTransform(JointTransformData data) {
		Matrix4f matrix = data.getJointLocalTransform();
		Vector3f translation = new Vector3f(matrix.m30, matrix.m31, matrix.m32);
		Quaternion rotation = new Quaternion(matrix);
		return new JointTransform(translation, rotation);
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {

	}

	@Module.Instance
	public static FlounderAnimation get() {
		return (FlounderAnimation) Framework.get().getInstance(FlounderAnimation.class);
	}

	@Module.TabName
	public static String getTab() {
		return "Animation";
	}
}
