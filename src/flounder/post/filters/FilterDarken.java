package flounder.post.filters;

import flounder.post.*;
import flounder.resources.*;

public class FilterDarken extends PostFilter {
	public FilterDarken() {
		super("filterDarken", new MyFile(PostFilter.POST_LOC, "darkenFragment.glsl"));
		super.storeUniforms();
	}

	@Override
	public void storeValues() {
	}
}
