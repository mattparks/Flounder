package flounder.post.filters;

import flounder.post.*;
import flounder.resources.*;

public class FilterGrey extends PostFilter {
	public FilterGrey() {
		super("filterGrey", new MyFile(PostFilter.POST_LOC, "greyFragment.glsl"));
	}

	@Override
	public void storeValues() {
	}
}
