package com.flounder.post.filters;

import com.flounder.post.*;
import com.flounder.resources.*;

public class FilterBloom1 extends PostFilter {
	public FilterBloom1() {
		super("filterBloom1", new MyFile(PostFilter.POST_LOC, "bloom1Fragment.glsl"));
	}

	@Override
	public void storeValues() {
	}
}
