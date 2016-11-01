package flounder.post.filters;

import flounder.engine.*;
import flounder.post.*;
import flounder.resources.*;

public class FilterDOF extends PostFilter {
	public FilterDOF() {
		super("filterDOF", new MyFile(PostFilter.POST_LOC, "dofFragment.glsl"));
	}

	@Override
	public void storeValues() {
		//	shader.getUniformFloat("aimDistance").loadFloat(FlounderEngine.getCamera().getAimDistance());
		shader.getUniformFloat("nearPlane").loadFloat(FlounderEngine.getCamera().getNearPlane());
		shader.getUniformFloat("farPlane").loadFloat(FlounderEngine.getCamera().getFarPlane());
	}
}
