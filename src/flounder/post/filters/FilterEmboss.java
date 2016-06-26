package flounder.post.filters;

import flounder.post.*;
import flounder.resources.*;

public class FilterEmboss extends PostFilter {
	public FilterEmboss() {
		super("filterEmboss", new MyFile(PostFilter.POST_LOC, "embossFragment.glsl"));
	}

	@Override
	public void storeValues() {
	}
}
