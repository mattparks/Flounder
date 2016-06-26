package flounder.post.filters;

import flounder.post.*;
import flounder.resources.*;

public class FilterNegative extends PostFilter {
	public FilterNegative() {
		super("filterNegative", new MyFile(PostFilter.POST_LOC, "negativeFragment.glsl"));
	}

	@Override
	public void storeValues() {
	}
}
