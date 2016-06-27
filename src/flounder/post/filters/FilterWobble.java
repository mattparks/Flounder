package flounder.post.filters;

import flounder.engine.*;
import flounder.post.*;
import flounder.resources.*;

public class FilterWobble extends PostFilter {
	private float wobbleAmount;

	public FilterWobble() {
		super("filterWobble", new MyFile(PostFilter.POST_LOC, "wobbleFragment.glsl"));
	}

	@Override
	public void storeValues() {
		shader.getUniformFloat("moveIt").loadFloat(wobbleAmount += 3 * FlounderEngine.getDelta());
	}
}
