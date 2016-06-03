package flounder.post.filters;

import flounder.devices.*;
import flounder.post.*;
import flounder.resources.*;
import flounder.shaders.*;
import flounder.textures.fbos.*;

public class FilterBlurHorizontal extends PostFilter {
	private UniformFloat width = new UniformFloat("width");
	private UniformFloat scale = new UniformFloat("scale");

	private int widthValue;
	private float scaleValue;
	private boolean fitToDisplay;

	public FilterBlurHorizontal(int widthValue, int heightValue) {
		super(new ShaderProgram("filterBlurHorizontal", VERTEX_LOCATION, new MyFile(PostFilter.POST_LOC, "blurHorizontalFragment.glsl")), FBO.newFBO(widthValue, heightValue).create());
		fitToDisplay = false;
		init(widthValue);
	}

	private void init(int widthValue) {
		super.storeUniforms(width, scale);
		this.widthValue = widthValue;
		this.scaleValue = 2.0f;
	}

	public FilterBlurHorizontal() {
		super(new ShaderProgram("filterBlurHorizontal", VERTEX_LOCATION, new MyFile(PostFilter.POST_LOC, "blurHorizontalFragment.glsl")), FBO.newFBO(FlounderDevices.getDisplay().getWidth(), FlounderDevices.getDisplay().getHeight()).fitToScreen().create());
		fitToDisplay = true;
		init(FlounderDevices.getDisplay().getWidth());
	}

	public void setScale(float scale) {
		this.scaleValue = scale;
	}

	@Override
	public void storeValues() {
		if (fitToDisplay) {
			widthValue = FlounderDevices.getDisplay().getWidth();
		}

		width.loadFloat(widthValue);
		scale.loadFloat(scaleValue);
	}
}
