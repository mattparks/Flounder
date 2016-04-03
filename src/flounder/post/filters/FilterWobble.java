package flounder.post.filters;

import flounder.engine.*;
import flounder.post.*;
import flounder.resources.*;
import flounder.shaders.*;

public class FilterWobble extends PostFilter {
	private final UniformFloat moveIt = new UniformFloat("moveIt");

	private float wobbleAmount;

	public FilterWobble() {
		super("filterWobble", new MyFile(PostFilter.POST_LOC, "wobbleFragment.glsl"));
		super.storeUniforms(moveIt);
	}

	@Override
	public void storeValues() {
		moveIt.loadFloat(wobbleAmount += 3 * FlounderEngine.getDelta());
	}
}
