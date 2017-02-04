package flounder.post.filters;

import flounder.camera.*;
import flounder.devices.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;
import flounder.post.*;
import flounder.resources.*;

public class FilterLensFlare extends PostFilter {
	private Vector2f sunPositon;
	private boolean flareHidden;

	public FilterLensFlare() {
		super("filterLensFlare", new MyFile(PostFilter.POST_LOC, "lensFlareFragment.glsl"));
		sunPositon = new Vector2f();
		flareHidden = false;
	}

	public void setSunPositon(Vector3f sunPositon) {
		Vector4f point4 = new Vector4f(sunPositon.x, sunPositon.y, sunPositon.z, 1);
		point4 = Matrix4f.transform(FlounderCamera.getCamera().getViewMatrix(), point4, null);
		point4 = Matrix4f.transform(FlounderCamera.getCamera().getProjectionMatrix(), point4, null);
		Vector3f point = new Vector3f(point4);

		point.x /= point.z;
		point.y /= point.z;


	//	point.x = (point.x + 1) * FlounderDisplay.getWidth() / 2;
	//	point.y = (point.y + 1) * FlounderDisplay.getHeight() / 2;

		this.sunPositon.set(point.x, point.y);
		this.flareHidden = point.z <= 0.0f;
	}

	@Override
	public void storeValues() {
		shader.getUniformVec2("sunPositon").loadVec2(sunPositon);
		shader.getUniformFloat("aspectRatio").loadFloat(FlounderDisplay.getAspectRatio());
		shader.getUniformBool("flareHidden").loadBoolean(flareHidden);
	}
}
