package flounder.post.filters;

import flounder.post.*;
import flounder.resources.*;

public class FilterDarken extends PostFilter {
	private float factorValue;

	public FilterDarken() {
		super("filterDarken", new MyFile(PostFilter.POST_LOC, "darkenFragment.glsl"));
		factorValue = 0.45f;
	}

	public float getFactorValue() {
		return factorValue;
	}

	public void setFactorValue(float factorValue) {
		this.factorValue = factorValue;
	}

	@Override
	public void storeValues() {
		shader.getUniformFloat("factor").loadFloat(factorValue);
	}
}
