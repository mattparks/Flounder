package flounder.post.filters;

import flounder.devices.*;
import flounder.maths.vectors.*;
import flounder.post.*;
import flounder.resources.*;
import flounder.shaders.*;
import flounder.textures.fbos.*;

public class FilterBlurVertical extends PostFilter {
	private final UniformFloat height = new UniformFloat("height");
	private final UniformFloat scale = new UniformFloat("scale");
	private final UniformVec4 blendSpread = new UniformVec4("blendSpread"); // 0 - 1, x being width min, y width being max, z being height min, w width height max..

	private int heightValue;
	private float scaleValue;
	private Vector4f blendSpreadValue;
	private boolean fitToDisplay;

	public FilterBlurVertical(final int widthValue, final int heightValue) {
		super(new ShaderProgram("filterBlurVertical", VERTEX_LOCATION, new MyFile(PostFilter.POST_LOC, "blurVerticalFragment.glsl")), FBO.newFBO(widthValue, heightValue).create());
		fitToDisplay = false;
		init(heightValue);
	}

	private void init(final int heightValue) {
		super.storeUniforms(height, scale, blendSpread);
		this.heightValue = heightValue;
		this.scaleValue = 2.0f;
		this.blendSpreadValue = new Vector4f(0.0f, 1.0f, 0.0f, 1.0f);
	}

	public FilterBlurVertical() {
		super(new ShaderProgram("filterBlurVertical", VERTEX_LOCATION, new MyFile(PostFilter.POST_LOC, "blurVerticalFragment.glsl")), FBO.newFBO(FlounderDevices.getDisplay().getWidth(), FlounderDevices.getDisplay().getHeight()).fitToScreen().create());
		fitToDisplay = true;
		init(FlounderDevices.getDisplay().getHeight());
	}

	public void setBlendSpread(final Vector4f blendSpreadValue) {
		this.blendSpreadValue = blendSpreadValue;
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
		blendSpread.loadVec4(blendSpreadValue);
	}
}
