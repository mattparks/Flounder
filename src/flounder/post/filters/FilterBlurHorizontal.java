package flounder.post.filters;

import flounder.devices.*;
import flounder.post.*;
import flounder.resources.*;
import flounder.shaders.*;
import flounder.textures.fbos.*;

public class FilterBlurHorizontal extends PostFilter {
	private final UniformFloat width = new UniformFloat("width");
	private final UniformFloat scale = new UniformFloat("scale");

	private int widthValue;
	private float scaleValue;
	private boolean fitToDisplay;

	public FilterBlurHorizontal(final int widthValue, final int heightValue) {
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

	public void setScale(final float scale) {
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
