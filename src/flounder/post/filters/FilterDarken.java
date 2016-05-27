package flounder.post.filters;

import flounder.post.*;
import flounder.resources.*;
import flounder.shaders.*;

public class FilterDarken extends PostFilter {
	private final UniformFloat factor = new UniformFloat("factor");
	private float factorValue;

	public FilterDarken() {
		super("filterDarken", new MyFile(PostFilter.POST_LOC, "darkenFragment.glsl"));
		super.storeUniforms(factor);
		factorValue = 0.45f;
	}

	public float getFactorValue() {
		return factorValue;
	}

	public void setFactorValue(final float factorValue) {
		this.factorValue = factorValue;
	}

	@Override
	public void storeValues() {
		factor.loadFloat(factorValue);
	}
}
