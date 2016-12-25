package flounder.animation;

import flounder.collada.*;
import flounder.collada.animation.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;
import flounder.resources.*;

import java.util.*;

/**
 * This class loads up an animation collada file, gets the information from it, and then creates and returns an animation from the extracted data.
 */
public class AnimationCreator {
	/**
	 * Loads up a collada animation file, and returns and animation created from the extracted animation data from the file.
	 *
	 * @param colladaFile The collada file to load from.
	 *
	 * @return The animation made from the data in the file.
	 */
	public static Animation loadAnimation(MyFile colladaFile) {
		AnimationData animationData = ColladaLoader.loadColladaAnimation(colladaFile);
		KeyFrame[] frames = new KeyFrame[animationData.keyFrames.length];
		int pointer = 0;

		for (KeyFrameData frameData : animationData.keyFrames) {
			frames[pointer++] = createKeyFrame(frameData);
		}

		return new Animation(animationData.lengthSeconds, frames);
	}

	private static KeyFrame createKeyFrame(KeyFrameData data) {
		Map<String, JointTransform> map = new HashMap<>();

		for (JointTransformData jointData : data.jointTransforms) {
			JointTransform jointTransform = createTransform(jointData);
			map.put(jointData.jointNameId, jointTransform);
		}

		return new KeyFrame(data.time, map);
	}

	private static JointTransform createTransform(JointTransformData data) {
		Matrix4f matrix = data.jointLocalTransform;
		Vector3f translation = new Vector3f(matrix.m30, matrix.m31, matrix.m32);
		Quaternion rotation = new Quaternion(matrix);
		return new JointTransform(translation, rotation);
	}
}
