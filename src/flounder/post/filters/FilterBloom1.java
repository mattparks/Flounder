package flounder.post.filters;

import flounder.post.*;
import flounder.resources.*;

public class FilterBloom1 extends PostFilter {
	private float bloomThreshold;

	public FilterBloom1() {
		super("filterBloom1", new MyFile(PostFilter.POST_LOC, "bloom1Fragment.glsl"));
		this.bloomThreshold = 0.85f;
	}

	@Override
	public void storeValues() {
		shader.getUniformFloat("bloomThreshold").loadFloat(bloomThreshold);
	}

	public float getBloomThreshold() {
		return bloomThreshold;
	}

	public void setBloomThreshold(float bloomThreshold) {
		this.bloomThreshold = bloomThreshold;
	}
}
