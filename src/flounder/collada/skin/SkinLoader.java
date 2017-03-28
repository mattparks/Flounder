package flounder.collada.skin;

import flounder.logger.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;
import flounder.parsing.xml.*;
import org.lwjgl.*;

import java.nio.*;
import java.util.*;

public class SkinLoader {
	private final XmlNode skinningData;

	private final int maxWeights;

	public SkinLoader(XmlNode controllersNode, int maxWeights) {
		this.skinningData = controllersNode.getChild("controller").getChild("skin");

		this.maxWeights = maxWeights;
	}

	public SkinningData extractSkinData() {
		List<String> jointsList = loadJointsList();

		Map<String, Matrix4f> bindPositions = loadBindPositions(jointsList);

		float[] weights = loadWeights();
		XmlNode weightsDataNode = skinningData.getChild("vertex_weights");
		int[] effectorJointCounts = getEffectiveJointsCounts(weightsDataNode);
		List<VertexSkinData> vertexWeights = getSkinData(weightsDataNode, effectorJointCounts, weights);

		return new SkinningData(jointsList, bindPositions, vertexWeights);
	}

	private List<String> loadJointsList() {
		XmlNode inputNode = skinningData.getChild("vertex_weights");
		String jointDataId = inputNode.getChildWithAttribute("input", "semantic", "JOINT").getAttribute("source").substring(1);
		XmlNode jointsNode = skinningData.getChildWithAttribute("source", "id", jointDataId).getChild("Name_array");
		String[] names = jointsNode.getData().split(" ");
		List<String> jointsList = new ArrayList<>();

		for (String name : names) {
			jointsList.add(name);
		}

		return jointsList;
	}

	private Map<String, Matrix4f> loadBindPositions(List<String> jointsList) {
		XmlNode jointsNode = skinningData.getChild("joints");
		String jointDataId = jointsNode.getChildWithAttribute("input", "semantic", "INV_BIND_MATRIX").getAttribute("source").substring(1);
		XmlNode bindPosesNode = skinningData.getChildWithAttribute("source", "id", jointDataId).getChild("float_array");
		String[] bindPoses = bindPosesNode.getData().split(" ");
		Map<String, Matrix4f> bindPosesList = new HashMap<>();

		float[] data = new float[16];
		int j = 0;
		int p = 0;

		for (int i = 0; i < bindPoses.length; i++) {
			data[j] = Float.parseFloat(bindPoses[i]);
			j++;

			if (j == 16) {
				Matrix4f transform = new Matrix4f(data);
				transform.transpose();
				bindPosesList.put(jointsList.get(p), transform);
				data = new float[16];
				j = 0;
				p++;
			}
		}

		return bindPosesList;
	}

	private float[] loadWeights() {
		XmlNode inputNode = skinningData.getChild("vertex_weights");
		String weightsDataId = inputNode.getChildWithAttribute("input", "semantic", "WEIGHT").getAttribute("source").substring(1);
		XmlNode weightsNode = skinningData.getChildWithAttribute("source", "id", weightsDataId).getChild("float_array");
		String[] rawData = weightsNode.getData().split(" ");
		float[] weights = new float[rawData.length];

		for (int i = 0; i < weights.length; i++) {
			weights[i] = Float.parseFloat(rawData[i]);
		}

		return weights;
	}

	private int[] getEffectiveJointsCounts(XmlNode weightsDataNode) {
		String[] rawData = weightsDataNode.getChild("vcount").getData().split(" ");
		int[] counts = new int[rawData.length];

		for (int i = 0; i < rawData.length; i++) {
			counts[i] = Integer.parseInt(rawData[i]);
		}

		return counts;
	}

	private List<VertexSkinData> getSkinData(XmlNode weightsDataNode, int[] counts, float[] weights) {
		String[] rawData = weightsDataNode.getChild("v").getData().split(" ");
		List<VertexSkinData> skinningData = new ArrayList<>();
		int pointer = 0;

		for (int count : counts) {
			VertexSkinData skinData = new VertexSkinData();

			for (int i = 0; i < count; i++) {
				int jointId = Integer.parseInt(rawData[pointer++]);
				int weightId = Integer.parseInt(rawData[pointer++]);
				skinData.addJointEffect(jointId, weights[weightId]);
			}

			skinData.limitJointNumber(maxWeights);
			skinningData.add(skinData);
		}

		return skinningData;
	}
}
