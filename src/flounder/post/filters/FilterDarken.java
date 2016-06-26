package flounder.post.filters;

import flounder.post.*;
import flounder.resources.*;
import flounder.shaders.*;

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
		((UniformFloat) shader.getUniform("factor")).loadFloat(factorValue);
	}
}
