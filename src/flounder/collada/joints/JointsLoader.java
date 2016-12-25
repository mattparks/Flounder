package flounder.collada.joints;

import flounder.maths.matrices.*;
import flounder.parsing.xml.*;
import org.lwjgl.*;

import java.nio.*;
import java.util.*;

public class JointsLoader {
	private XmlNode armatureData;

	private List<String> boneOrder;

	private int jointCount;

	public JointsLoader(XmlNode visualSceneNode, List<String> boneOrder) {
		this.armatureData = visualSceneNode.getChild("visual_scene").getChildWithAttribute("node", "id", "Armature");

		this.boneOrder = boneOrder;

		this.jointCount = 0;
	}

	public JointsData extractBoneData() {
		XmlNode headNode = armatureData.getChild("node");
		JointData headJoint = loadJointData(headNode);
		return new JointsData(jointCount, headJoint);
	}

	private JointData loadJointData(XmlNode jointNode) {
		JointData joint = extractMainJointData(jointNode);

		for (XmlNode childNode : jointNode.getChildren("node")) {
			joint.addChild(loadJointData(childNode));
		}

		return joint;
	}

	private JointData extractMainJointData(XmlNode jointNode) {
		String nameId = jointNode.getAttribute("id");
		int index = boneOrder.indexOf(nameId);
		String[] matrixData = jointNode.getChild("matrix").getData().split(" ");
		Matrix4f matrix = new Matrix4f();
		matrix.load(convertData(matrixData));
		matrix.transpose();
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
