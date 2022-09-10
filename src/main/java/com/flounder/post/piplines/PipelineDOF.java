package com.flounder.post.piplines;

import com.flounder.fbos.*;
import com.flounder.post.*;
import com.flounder.post.filters.*;

public class PipelineDOF extends PostPipeline {
	private static final int BLUR_TEXTURE_WIDTH = 256;
	private static final int BLUR_TEXTURE_HEIGHT = 144;

	private PipelineGaussian pipelineGaussian;
	private FilterFXAA filterFXAA;
	private FilterDOF filterDOF;

	public PipelineDOF() {
		pipelineGaussian = new PipelineGaussian(BLUR_TEXTURE_WIDTH, BLUR_TEXTURE_HEIGHT);
		filterFXAA = new FilterFXAA();
		filterDOF = new FilterDOF();
	}

	@Override
	public void renderPipeline(int... textures) {
		filterFXAA.applyFilter(textures);
		pipelineGaussian.setScale(0.5f);
		pipelineGaussian.renderPipeline(filterFXAA.fbo.getColourTexture(0));
		filterDOF.applyFilter(
				filterFXAA.fbo.getColourTexture(0), // Original.
				textures[textures.length - 1], // Depth texture.
				pipelineGaussian.getOutput().getColourTexture(0) // Blurred
		);
	}

	@Override
	public FBO getOutput() {
		return filterDOF.fbo;
	}

	@Override
	public void dispose() {
		pipelineGaussian.dispose();
		filterFXAA.dispose();
		filterDOF.dispose();
	}
}
