package flounder.post.filters;

import flounder.post.*;
import flounder.resources.*;
import flounder.shaders.*;

public class FilterFXAA extends PostFilter {
	private final UniformFloat spanMax = new UniformFloat("spanMax");
	private float spanMaxValue;

	public FilterFXAA() {
		super("filterFXAA", new MyFile(PostFilter.POST_LOC, "fxaaFragment.glsl"));
		super.storeUniforms(spanMax);
		spanMaxValue = 8.0f;
	}

	public float getSpanMaxValue() {
		return spanMaxValue;
	}

	public void setSpanMaxValue(final float spanMaxValue) {
		this.spanMaxValue = spanMaxValue;
	}

	@Override
	public void storeValues() {
		spanMax.loadFloat(spanMaxValue);
	}
}
