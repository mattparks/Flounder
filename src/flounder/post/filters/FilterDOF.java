package flounder.post.filters;

import flounder.camera.*;
import flounder.post.*;
import flounder.resources.*;

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
