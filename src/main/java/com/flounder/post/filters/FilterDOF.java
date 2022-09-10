package com.flounder.post.filters;

import com.flounder.camera.*;
import com.flounder.post.*;
import com.flounder.resources.*;

public class FilterDOF extends PostFilter {
	public FilterDOF() {
		super("filterDOF", new MyFile(PostFilter.POST_LOC, "dofFragment.glsl"));
	}

	@Override
	public void storeValues() {
		shader.getUniformFloat("aimDistance").loadFloat(8.24621125124f);
		shader.getUniformFloat("nearPlane").loadFloat(FlounderCamera.get().getCamera().getNearPlane());
		shader.getUniformFloat("farPlane").loadFloat(FlounderCamera.get().getCamera().getFarPlane());
	}
}
