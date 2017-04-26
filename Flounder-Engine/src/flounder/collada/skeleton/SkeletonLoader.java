package flounder.collada.skeleton;

import flounder.collada.*;
import flounder.maths.matrices.*;
import flounder.parsing.xml.*;
import org.lwjgl.*;

import java.nio.*;
import java.util.*;

public class SkeletonLoader {
	private XmlNode armatureData;

	private List<String> boneOrder;
	private int jointCount;

	public SkeletonLoader(XmlNode visualSceneNode, List<String> boneOrder) {
		this.armatureData = visualSceneNode.getChild("visual_scene").getChildWithAttribute("node", "id", "Armature");

		this.boneOrder = boneOrder;
		this.jointCount = 0;
	}

	public SkeletonData extractBoneData() {
		XmlNode headNode = armatureData.getChild("node");
		JointData headJoint = loadJointData(headNode, true);
		return new SkeletonData(jointCount, headJoint);
	}

	private JointData loadJointData(XmlNode jointNode, boolean isRoot) {
		JointData joint = extractMainJointData(jointNode, isRoot);

		for (XmlNode childNode : jointNode.getChildren("node")) {
			joint.addChild(loadJointData(childNode, false));
		}

		return joint;
	}

	private JointData extractMainJointData(XmlNode jointNode, boolean isRoot) {
		String nameId = jointNode.getAttribute("id");
		int index = boneOrder.indexOf(nameId);
		String[] matrixData = jointNode.getChild("matrix").getData().split(" ");
		Matrix4f matrix = new Matrix4f();
		matrix.load(convertData(matrixData));
		matrix.transpose();

		if (isRoot) {
			// Because in Blender z is up, but in our game y is up.
			Matrix4f.multiply(FlounderCollada.CORRECTION, matrix, matrix);
		}

		jointCount++;
		return new JointData(index, nameId, matrix);
	}

	private FloatBuffer convertData(String[] rawData) {
		float[] matrixData = new float[16];

		for (int i = 0; i < matrixData.length; i++) {
			matrixData[i] = Float.parseFloat(rawData[i]);
		}

		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		buffer.put(matrixData);
		buffer.flip();
		return buffer;
	}
}
