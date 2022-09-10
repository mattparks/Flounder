package com.flounder.post.filters;

import com.flounder.post.*;
import com.flounder.resources.*;

public class FilterGrey extends PostFilter {
	public FilterGrey() {
		super("filterGrey", new MyFile(PostFilter.POST_LOC, "greyFragment.glsl"));
	}

	@Override
	public void storeValues() {
	}
}
