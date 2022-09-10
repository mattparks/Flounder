package com.flounder.post.filters;

import com.flounder.framework.*;
import com.flounder.post.*;
import com.flounder.resources.*;

public class FilterWobble extends PostFilter {
	private float wobbleAmount;

	public FilterWobble() {
		super("filterWobble", new MyFile(PostFilter.POST_LOC, "wobbleFragment.glsl"));
	}

	@Override
	public void storeValues() {
		shader.getUniformFloat("moveIt").loadFloat(wobbleAmount += 2.0f * Framework.get().getDeltaRender());
	}
}
