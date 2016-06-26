package flounder.post.filters;

import flounder.engine.*;
import flounder.fbos.*;
import flounder.post.*;
import flounder.resources.*;
import flounder.shaders.*;

public class FilterBlurVertical extends PostFilter {
	private int heightValue;
	private float scaleValue;
	private boolean fitToDisplay;
	private float sizeScalar;

	public FilterBlurVertical(float sizeScalar) {
		super(new ShaderProgram("filterBlurVertical", VERTEX_LOCATION, new MyFile(PostFilter.POST_LOC, "blurVerticalFragment.glsl")), FBO.newFBO(FlounderEngine.getDevices().getDisplay().getWidth(), FlounderEngine.getDevices().getDisplay().getHeight()).fitToScreen(1.0f).create());
		fitToDisplay = true;
		this.sizeScalar = sizeScalar;
		init((int) (FlounderEngine.getDevices().getDisplay().getHeight() * sizeScalar));
	}

	public FilterBlurVertical(int widthValue, int heightValue) {
		super(new ShaderProgram("filterBlurVertical", VERTEX_LOCATION, new MyFile(PostFilter.POST_LOC, "blurVerticalFragment.glsl")), FBO.newFBO(widthValue, heightValue).create());
		fitToDisplay = false;
		this.sizeScalar = 1.0f;
		init(heightValue);
	}

	private void init(int heightValue) {
		this.heightValue = heightValue;
		this.scaleValue = 2.0f;
	}

	public void setScale(float scale) {
		this.scaleValue = scale;
	}

	@Override
	public void storeValues() {
		if (fitToDisplay) {
			heightValue = (int) (FlounderEngine.getDevices().getDisplay().getHeight() * sizeScalar);
		}

		shader.getUniformFloat("height").loadFloat(heightValue);
		shader.getUniformFloat("scale").loadFloat(scaleValue);
	}
}
