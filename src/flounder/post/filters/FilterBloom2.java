package flounder.post.filters;

import flounder.post.*;
import flounder.resources.*;

public class FilterBloom2 extends PostFilter {
	public FilterBloom2() {
		super("filterBloom2", new MyFile(PostFilter.POST_LOC, "bloom2Fragment.glsl"));
		super.storeUniforms();
	}

	@Override
	public void storeValues() {
	}
}
