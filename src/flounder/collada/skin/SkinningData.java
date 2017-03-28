package flounder.collada.skin;

import flounder.maths.matrices.*;

import java.util.*;

public class SkinningData {
	private final List<String> jointOrder;
	private final Map<String, Matrix4f> bindPositions;
	private final List<VertexSkinData> verticesSkinData;

	protected SkinningData(List<String> jointOrder, Map<String, Matrix4f> bindPositions, List<VertexSkinData> verticesSkinData) {
		this.jointOrder = jointOrder;
		this.bindPositions = bindPositions;
		this.verticesSkinData = verticesSkinData;
	}

	public List<String> getJointOrder() {
		return jointOrder;
	}

	public Map<String, Matrix4f> getBindPositions() {
		return bindPositions;
	}

	public List<VertexSkinData> getVerticesSkinData() {
		return verticesSkinData;
	}
}
