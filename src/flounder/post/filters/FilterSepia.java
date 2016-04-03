package flounder.post.filters;

import flounder.post.*;
import flounder.resources.*;

public class FilterSepia extends PostFilter {
	public FilterSepia() {
		super("filterSepia", new MyFile(PostFilter.POST_LOC, "sepiaFragment.glsl"));
		super.storeUniforms();
	}

	@Override
	public void storeValues() {
	}
}
