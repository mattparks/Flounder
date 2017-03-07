package flounder.post.filters;

import flounder.camera.*;
import flounder.devices.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.post.*;
import flounder.resources.*;

public class FilterLensFlare extends PostFilter {
	private Vector3f sunPosition;
	private float worldHeight;

	public FilterLensFlare() {
		super("filterLensFlare", new MyFile(PostFilter.POST_LOC, "lensFlareFragment.glsl"));
		this.sunPosition = new Vector3f();
		this.worldHeight = 0.0f;
	}

	public void setSunPosition(Vector3f sunPosition) {
		Maths.worldToScreenSpace(sunPosition, FlounderCamera.getCamera().getViewMatrix(), FlounderCamera.getCamera().getProjectionMatrix(), this.sunPosition);
		this.worldHeight = sunPosition.getY();
	}

	@Override
	public void storeValues() {
		shader.getUniformVec3("sunPosition").loadVec3(sunPosition);
		shader.getUniformFloat("worldHeight").loadFloat(worldHeight);
		shader.getUniformFloat("aspectRatio").loadFloat(FlounderDisplay.getAspectRatio());
	}
}