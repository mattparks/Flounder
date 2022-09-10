package com.flounder.post.filters;

import com.flounder.post.*;
import com.flounder.resources.*;

public class FilterNegative extends PostFilter {
	public FilterNegative() {
		super("filterNegative", new MyFile(PostFilter.POST_LOC, "negativeFragment.glsl"));
	}

	@Override
	public void storeValues() {
	}
}
