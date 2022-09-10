package com.flounder.post.filters;

import com.flounder.post.*;
import com.flounder.resources.*;

public class FilterTone extends PostFilter {
	public FilterTone() {
		super("filterTone", new MyFile(PostFilter.POST_LOC, "toneFragment.glsl"));
	}

	@Override
	public void storeValues() {
	}
}
