package flounder.collada.skin;

import java.util.*;

public class SkinningData {
	private final List<String> jointOrder;
	private final List<VertexSkinData> verticesSkinData;

	protected SkinningData(List<String> jointOrder, List<VertexSkinData> verticesSkinData) {
		this.jointOrder = jointOrder;
		this.verticesSkinData = verticesSkinData;
	}

	public List<String> getJointOrder() {
		return jointOrder;
	}

	public List<VertexSkinData> getVerticesSkinData() {
		return verticesSkinData;
	}
}
