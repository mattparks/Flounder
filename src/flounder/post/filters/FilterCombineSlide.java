package flounder.post.filters;

import flounder.maths.vectors.*;
import flounder.post.*;
import flounder.resources.*;
import flounder.shaders.*;

public class FilterCombineSlide extends PostFilter {
	private UniformVec4 slideSpace = new UniformVec4("slideSpace"); // 0 - 1, x being width min, y width being max, z being height min, w width height max..

	private Vector4f slideSpaceValue;

	public FilterCombineSlide() {
		super("combineSlide", new MyFile(PostFilter.POST_LOC, "combineSlideFragment.glsl"));
		super.storeUniforms(slideSpace);
		this.slideSpaceValue = new Vector4f(0.0f, 1.0f, 0.0f, 1.0f);
	}

	public void setSlideSpace(float x, float y, float z, float w) {
		this.slideSpaceValue.set(x, y, z, w);
	}

	@Override
	public void storeValues() {
		slideSpace.loadVec4(slideSpaceValue);
	}
}
