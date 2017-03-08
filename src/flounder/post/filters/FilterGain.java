package flounder.post.filters;

import flounder.framework.*;
import flounder.post.*;
import flounder.resources.*;

public class FilterGain extends PostFilter {
	public FilterGain() {
		super("filterGrain", new MyFile(PostFilter.POST_LOC, "grainFragment.glsl"));
	}

	@Override
	public void storeValues() {
		shader.getUniformFloat("time").loadFloat(Framework.getTimeSec());
	}
}
