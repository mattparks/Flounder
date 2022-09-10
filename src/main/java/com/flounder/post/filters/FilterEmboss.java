package com.flounder.post.filters;

import com.flounder.post.*;
import com.flounder.resources.*;

public class FilterEmboss extends PostFilter {
	public FilterEmboss() {
		super("filterEmboss", new MyFile(PostFilter.POST_LOC, "embossFragment.glsl"));
	}

	@Override
	public void storeValues() {
	}
}
