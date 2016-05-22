package flounder.post.filters;

import flounder.devices.*;
import flounder.maths.vectors.*;
import flounder.post.*;
import flounder.resources.*;
import flounder.shaders.*;
import flounder.textures.fbos.*;

public class FilterBlurHorizontal extends PostFilter {
	private final UniformFloat width = new UniformFloat("width");
	private final UniformFloat scale = new UniformFloat("scale");
	private final UniformVec4 blendSpread = new UniformVec4("blendSpread"); // 0 - 1, x being width min, y width being max, z being height min, w width height max..

	private int widthValue;
	private float scaleValue;
	private Vector4f blendSpreadValue;
	private boolean fitToDisplay;

	public FilterBlurHorizontal(final int widthValue, final int heightValue) {
		super(new ShaderProgram("filterBlurHorizontal", VERTEX_LOCATION, new MyFile(PostFilter.POST_LOC, "blurHorizontalFragment.glsl")), FBO.newFBO(widthValue, heightValue).create());
		fitToDisplay = false;
		init(widthValue);
	}

	private void init(int widthValue) {
		super.storeUniforms(width, scale, blendSpread);
		this.widthValue = widthValue;
		this.scaleValue = 2.0f;
		this.blendSpreadValue = new Vector4f(0.0f, 1.0f, 0.0f, 1.0f);
	}

	public FilterBlurHorizontal() {
		super(new ShaderProgram("filterBlurHorizontal", VERTEX_LOCATION, new MyFile(PostFilter.POST_LOC, "blurHorizontalFragment.glsl")), FBO.newFBO(ManagerDevices.getDisplay().getWidth(), ManagerDevices.getDisplay().getHeight()).fitToScreen().create());
		fitToDisplay = true;
		init(ManagerDevices.getDisplay().getWidth());
	}

	public void setBlendSpread(final Vector4f blendSpreadValue) {
		this.blendSpreadValue.set(blendSpreadValue);
	}

	public void setScale(final float scale) {
		this.scaleValue = scale;
	}

	@Override
	public void storeValues() {
		if (fitToDisplay) {
			widthValue = ManagerDevices.getDisplay().getWidth();
		}

		width.loadFloat(widthValue);
		scale.loadFloat(scaleValue);
		blendSpread.loadVec4(blendSpreadValue);
	}
}
