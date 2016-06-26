package flounder.post.filters;

import flounder.engine.*;
import flounder.post.*;
import flounder.resources.*;
import flounder.shaders.*;

public class FilterDOF extends PostFilter {
	public FilterDOF() {
		super("filterDOF", new MyFile(PostFilter.POST_LOC, "dofFragment.glsl"));
	}

	@Override
	public void storeValues() {
		((UniformFloat) shader.getUniform("aimDistance")).loadFloat(FlounderEngine.getCamera().getAimDistance());
		((UniformFloat) shader.getUniform("nearPlane")).loadFloat(FlounderEngine.getCamera().getNearPlane());
		((UniformFloat) shader.getUniform("farPlane")).loadFloat(FlounderEngine.getCamera().getFarPlane());
	}
}
