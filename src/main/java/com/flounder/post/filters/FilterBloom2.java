package com.flounder.post.filters;

import com.flounder.post.*;
import com.flounder.resources.*;

public class FilterBloom2 extends PostFilter {
	public FilterBloom2() {
		super("filterBloom2", new MyFile(PostFilter.POST_LOC, "bloom2Fragment.glsl"));
	}

	@Override
	public void storeValues() {
	}
}
