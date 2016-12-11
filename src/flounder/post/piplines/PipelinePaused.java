package flounder.post.piplines;

import flounder.fbos.*;
import flounder.post.*;
import flounder.post.filters.*;

public class PipelinePaused extends PostPipeline {
	private FilterDarken filterDarken;
	private FBO pipelineGaussian1;
	private PipelineGaussian pipelineGaussian2;
	private FilterCombineSlide filterCombineSlide;

	private float blurFactor;

	public PipelinePaused() {
		filterDarken = new FilterDarken();
		pipelineGaussian1 = FBO.newFBO(1.0f / 10.0f).depthBuffer(DepthBufferType.NONE).create();
		pipelineGaussian2 = new PipelineGaussian(1.0f / 7.0f);
		filterCombineSlide = new FilterCombineSlide();

		blurFactor = 0.0f;
	}

	@Override
	public void renderPipeline(FBO startFBO) {
		startFBO.resolveFBO(0, 0, pipelineGaussian1);

		pipelineGaussian2.setScale(1.25f);
		pipelineGaussian2.renderPipeline(pipelineGaussian1);

		filterDarken.setFactorValue(Math.max(Math.abs(1.0f - blurFactor), 0.45f));
		filterDarken.applyFilter(pipelineGaussian2.getOutput().getColourTexture(0));

		filterCombineSlide.setSlideSpace(blurFactor, 1.0f, 0.0f, 1.0f);
		filterCombineSlide.applyFilter(startFBO.getColourTexture(0), filterDarken.fbo.getColourTexture(0));
	}

	@Override
	public FBO getOutput() {
		return filterCombineSlide.fbo;
	}

	public void setBlurFactor(float blurFactor) {
		this.blurFactor = blurFactor;
	}

	@Override
	public void dispose() {
		filterDarken.dispose();
		pipelineGaussian1.delete();
		pipelineGaussian2.dispose();
		filterCombineSlide.dispose();
	}
}
