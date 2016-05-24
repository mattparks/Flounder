package flounder.post.filters;

import flounder.devices.*;
import flounder.post.*;
import flounder.resources.*;
import flounder.shaders.*;
import flounder.textures.fbos.*;

public class FilterBlurVertical extends PostFilter {
	private final UniformFloat height = new UniformFloat("height");
	private final UniformFloat scale = new UniformFloat("scale");

	private int heightValue;
	private float scaleValue;
	private boolean fitToDisplay;

	public FilterBlurVertical(final int widthValue, final int heightValue) {
		super(new ShaderProgram("filterBlurVertical", VERTEX_LOCATION, new MyFile(PostFilter.POST_LOC, "blurVerticalFragment.glsl")), FBO.newFBO(widthValue, heightValue).create());
		fitToDisplay = false;
		init(heightValue);
	}

	private void init(final int heightValue) {
		super.storeUniforms(height, scale);
		this.heightValue = heightValue;
		this.scaleValue = 2.0f;
	}

	public FilterBlurVertical() {
		super(new ShaderProgram("filterBlurVertical", VERTEX_LOCATION, new MyFile(PostFilter.POST_LOC, "blurVerticalFragment.glsl")), FBO.newFBO(FlounderDevices.getDisplay().getWidth(), FlounderDevices.getDisplay().getHeight()).fitToScreen().create());
		fitToDisplay = true;
		init(FlounderDevices.getDisplay().getHeight());
	}

	public void setScale(final float scale) {
		this.scaleValue = scale;
	}

	@Override
	public void storeValues() {
		if (fitToDisplay) {
			heightValue = FlounderDevices.getDisplay().getHeight();
		}

		height.loadFloat(heightValue);
		scale.loadFloat(scaleValue);
	}
}
