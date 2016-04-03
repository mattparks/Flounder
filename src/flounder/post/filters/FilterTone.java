package flounder.post.filters;

import flounder.post.*;
import flounder.resources.*;

public class FilterTone extends PostFilter {
	public FilterTone() {
		super("filterTone", new MyFile(PostFilter.POST_LOC, "toneFragment.glsl"));
		super.storeUniforms();
	}

	@Override
	public void storeValues() {
	}
}
