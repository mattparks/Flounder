package com.flounder.post.piplines;

import com.flounder.fbos.*;
import com.flounder.post.*;
import com.flounder.post.filters.*;

public class PipelineGaussian extends PostPipeline {
	private FilterBlurHorizontal filterBlurHorizontal;
	private FilterBlurVertical filterBlurVertical;

	public PipelineGaussian(int width, int height) {
		filterBlurHorizontal = new FilterBlurHorizontal(width, height);
		filterBlurVertical = new FilterBlurVertical(width, height);
	}

	public PipelineGaussian(float sizeScalar) {
		filterBlurHorizontal = new FilterBlurHorizontal(sizeScalar);
		filterBlurVertical = new FilterBlurVertical(sizeScalar);
	}

	@Override
	public void renderPipeline(int... textures) {
		filterBlurHorizontal.applyFilter(textures);
		filterBlurVertical.applyFilter(filterBlurHorizontal.fbo.getColourTexture(0));
	}

	@Override
	public FBO getOutput() {
		return filterBlurVertical.fbo;
	}

	public void setScale(float scale) {
		filterBlurHorizontal.setScale(scale);
		filterBlurVertical.setScale(scale);
	}

	@Override
	public void dispose() {
		filterBlurHorizontal.dispose();
		filterBlurVertical.dispose();
	}
}
