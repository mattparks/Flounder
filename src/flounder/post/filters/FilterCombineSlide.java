package flounder.post.filters;

import flounder.maths.vectors.*;
import flounder.post.*;
import flounder.resources.*;

public class FilterCombineSlide extends PostFilter {
	private Vector4f slideSpaceValue; // 0 - 1, x being width min, y width being max, z being height min, w width height max.

	public FilterCombineSlide() {
		super("combineSlide", new MyFile(PostFilter.POST_LOC, "combineSlideFragment.glsl"));
		this.slideSpaceValue = new Vector4f(0.0f, 1.0f, 0.0f, 1.0f);
	}

	public void setSlideSpace(float x, float y, float z, float w) {
		this.slideSpaceValue.set(x, y, z, w);
	}

	@Override
	public void storeValues() {
		shader.getUniformVec4("slideSpace").loadVec4(slideSpaceValue);
	}
}
