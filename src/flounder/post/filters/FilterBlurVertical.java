package flounder.post.filters;

import flounder.engine.*;
import flounder.fbos.*;
import flounder.post.*;
import flounder.resources.*;
import flounder.shaders.*;

public class FilterBlurVertical extends PostFilter {
	private UniformFloat height = new UniformFloat("height");
	private UniformFloat scale = new UniformFloat("scale");

	private int heightValue;
	private float scaleValue;
	private boolean fitToDisplay;

	public FilterBlurVertical(int widthValue, int heightValue) {
		super(new ShaderProgram("filterBlurVertical", VERTEX_LOCATION, new MyFile(PostFilter.POST_LOC, "blurVerticalFragment.glsl")), FBO.newFBO(widthValue, heightValue).create());
		fitToDisplay = false;
		init(heightValue);
	}

	private void init(int heightValue) {
		super.storeUniforms(height, scale);
		this.heightValue = heightValue;
		this.scaleValue = 2.0f;
	}

	public FilterBlurVertical() {
		super(new ShaderProgram("filterBlurVertical", VERTEX_LOCATION, new MyFile(PostFilter.POST_LOC, "blurVerticalFragment.glsl")), FBO.newFBO(FlounderEngine.getDevices().getDisplay().getWidth(), FlounderEngine.getDevices().getDisplay().getHeight()).fitToScreen().create());
		fitToDisplay = true;
		init(FlounderEngine.getDevices().getDisplay().getHeight());
	}

	public void setScale(float scale) {
		this.scaleValue = scale;
	}

	@Override
	public void storeValues() {
		if (fitToDisplay) {
			heightValue = FlounderEngine.getDevices().getDisplay().getHeight();
		}

		height.loadFloat(heightValue);
		scale.loadFloat(scaleValue);
	}
}
