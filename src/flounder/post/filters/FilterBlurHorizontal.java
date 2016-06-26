package flounder.post.filters;

import flounder.engine.*;
import flounder.fbos.*;
import flounder.post.*;
import flounder.resources.*;
import flounder.shaders.*;

public class FilterBlurHorizontal extends PostFilter {
	private int widthValue;
	private float scaleValue;
	private boolean fitToDisplay;
	private float sizeScalar;

	public FilterBlurHorizontal(float sizeScalar) {
		super(new ShaderProgram("filterBlurHorizontal", VERTEX_LOCATION, new MyFile(PostFilter.POST_LOC, "blurHorizontalFragment.glsl")), FBO.newFBO(sizeScalar).create());
		fitToDisplay = true;
		this.sizeScalar = sizeScalar;
		init((int) (FlounderEngine.getDevices().getDisplay().getWidth() * sizeScalar));
	}

	public FilterBlurHorizontal(int widthValue, int heightValue) {
		super(new ShaderProgram("filterBlurHorizontal", VERTEX_LOCATION, new MyFile(PostFilter.POST_LOC, "blurHorizontalFragment.glsl")), FBO.newFBO(widthValue, heightValue).create());
		fitToDisplay = false;
		this.sizeScalar = 1.0f;
		init(widthValue);
	}

	private void init(int widthValue) {
		this.widthValue = widthValue;
		this.scaleValue = 2.0f;
	}

	public void setScale(float scale) {
		this.scaleValue = scale;
	}

	@Override
	public void storeValues() {
		if (fitToDisplay) {
			widthValue = (int) (FlounderEngine.getDevices().getDisplay().getWidth() * sizeScalar);
		}

		shader.getUniformFloat("width").loadFloat(widthValue);
		shader.getUniformFloat("scale").loadFloat(scaleValue);
	}
}
