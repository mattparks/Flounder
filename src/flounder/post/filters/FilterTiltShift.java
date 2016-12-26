package flounder.post.filters;

import flounder.post.*;
import flounder.resources.*;

public class FilterTiltShift extends PostFilter {
	public FilterTiltShift() {
		super("filterTiltShift", new MyFile(PostFilter.POST_LOC, "tiltShiftFragment.glsl"));
	}

	@Override
	public void storeValues() {
	}
}
