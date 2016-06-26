package flounder.post.filters;

import flounder.post.*;
import flounder.resources.*;

public class FilterPixel extends PostFilter {
	public FilterPixel() {
		super("filterPixel", new MyFile(PostFilter.POST_LOC, "pixelFragment.glsl"));
	}

	@Override
	public void storeValues() {
	}
}
