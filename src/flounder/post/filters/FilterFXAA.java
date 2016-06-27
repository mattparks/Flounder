package flounder.post.filters;

import flounder.post.*;
import flounder.resources.*;

public class FilterFXAA extends PostFilter {
	private float spanMaxValue;

	public FilterFXAA() {
		super("filterFXAA", new MyFile(PostFilter.POST_LOC, "fxaaFragment.glsl"));
		spanMaxValue = 8.0f;
	}

	public float getSpanMaxValue() {
		return spanMaxValue;
	}

	public void setSpanMaxValue(float spanMaxValue) {
		this.spanMaxValue = spanMaxValue;
	}

	@Override
	public void storeValues() {
		shader.getUniformFloat("spanMax").loadFloat(spanMaxValue);
	}
}
