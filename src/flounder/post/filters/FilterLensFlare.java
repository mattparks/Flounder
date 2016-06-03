package flounder.post.filters;

import flounder.maths.vectors.*;
import flounder.post.*;
import flounder.resources.*;
import flounder.shaders.*;

public class FilterLensFlare extends PostFilter {
	private UniformVec2 cameraPosition = new UniformVec2("cameraPosition");
	private Vector3f sunPositon;

	public FilterLensFlare() {
		super("filterLensFlare", new MyFile(PostFilter.POST_LOC, "lensFlareFragment.glsl"));
		super.storeUniforms(cameraPosition);
		sunPositon = new Vector3f();
	}

	public void setSunPositon(Vector3f sunPositon) {
		this.sunPositon.set(sunPositon);
	}

	@Override
	public void storeValues() {
		// TODO: Move camera from 3D space to screen 2D.
		float cameraX = 1.0f;
		float cameraY = 0.0f;
		cameraPosition.loadVec2(cameraX, cameraY);
	}
}
