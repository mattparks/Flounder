package flounder.post.filters;

import flounder.engine.*;
import flounder.post.*;
import flounder.resources.*;
import flounder.shaders.*;

public class FilterWobble extends PostFilter {
	private float wobbleAmount;

	public FilterWobble() {
		super("filterWobble", new MyFile(PostFilter.POST_LOC, "wobbleFragment.glsl"));
	}

	@Override
	public void storeValues() {
		((UniformFloat) shader.getUniform("moveIt")).loadFloat(wobbleAmount += 3 * FlounderEngine.getDelta());
	}
}
