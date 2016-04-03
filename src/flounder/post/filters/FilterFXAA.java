package flounder.post.filters;

import flounder.post.*;
import flounder.resources.*;

public class FilterFXAA extends PostFilter {
	public FilterFXAA() {
		super("filterFXAA", new MyFile(PostFilter.POST_LOC, "fxaaFragment.glsl"));
		super.storeUniforms();
	}

	@Override
	public void storeValues() {
	}
}
