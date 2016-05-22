package flounder.post.piplines;

import flounder.maths.vectors.*;
import flounder.post.*;
import flounder.post.filters.*;
import flounder.textures.fbos.*;

public class PipelineGaussian extends PostPipeline {
	private final FilterBlurHorizontal filterBlurHorizontal;
	private final FilterBlurVertical filterBlurVertical;
	private final Vector4f blendSpreadValue; // 0 - 1, x being width min, y width being max, z being height min, w width height max..

	public PipelineGaussian(final int width, final int height, final boolean fitToDisplay) {
		filterBlurHorizontal = fitToDisplay ? new FilterBlurHorizontal() : new FilterBlurHorizontal(width, height);
		filterBlurVertical = fitToDisplay ? new FilterBlurVertical() : new FilterBlurVertical(width, height);
		this.blendSpreadValue = new Vector4f(0.0f, 1.0f, 0.0f, 1.0f);
	}

	@Override
	public void renderPipeline(final FBO startFBO) {
		filterBlurHorizontal.setBlendSpread(blendSpreadValue);
		filterBlurVertical.setBlendSpread(blendSpreadValue);

		filterBlurHorizontal.applyFilter(startFBO.getColourTexture());
		filterBlurVertical.applyFilter(filterBlurHorizontal.fbo.getColourTexture());
	}

	@Override
	public FBO getOutput() {
		return filterBlurVertical.fbo;
	}

	@Override
	public void dispose() {
		filterBlurHorizontal.dispose();
		filterBlurVertical.dispose();
	}

	public void setBlendSpreadValue(final float x, final float y, final float z, final float w) {
		this.blendSpreadValue.set(x, y, z, w);
	}

	public void setBlendSpreadValue(final Vector4f spreadValue) {
		this.blendSpreadValue.set(spreadValue);
	}

	public void setScale(final float scale) {
		filterBlurHorizontal.setScale(scale);
		filterBlurVertical.setScale(scale);
	}
}
