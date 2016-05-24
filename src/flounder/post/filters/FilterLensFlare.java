package flounder.post.filters;

import flounder.post.*;
import flounder.resources.*;
import flounder.shaders.*;

public class FilterLensFlare extends PostFilter {
	private final UniformVec2 cameraPosition = new UniformVec2("cameraPosition");

	public FilterLensFlare() {
		super("filterLensFlare", new MyFile(PostFilter.POST_LOC, "lensFlareFragment.glsl"));
		super.storeUniforms(cameraPosition);
	}

	@Override
	public void storeValues() {
		final float cameraX = 1.0f;
		final float cameraY = 0.0f;
		cameraPosition.loadVec2(cameraX, cameraY);
	}
}
