package com.flounder.post.filters;

import com.flounder.post.*;
import com.flounder.resources.*;

public class FilterSepia extends PostFilter {
	public FilterSepia() {
		super("filterSepia", new MyFile(PostFilter.POST_LOC, "sepiaFragment.glsl"));
	}

	@Override
	public void storeValues() {
	}
}
