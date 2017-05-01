package flounder.post.piplines;

import flounder.fbos.*;
import flounder.post.*;
import flounder.post.filters.*;

public class PipelinePaused extends PostPipeline {
	private FilterDarken filterDarken;
	private PipelineGaussian pipelineGaussian1;
	private PipelineGaussian pipelineGaussian2;
	private FilterCombineSlide filterCombineSlide;

	private float blurFactor;

	public PipelinePaused() {
		filterDarken = new FilterDarken();
		pipelineGaussian1 = new PipelineGaussian(1.0f / 10.0f);
		pipelineGaussian2 = new PipelineGaussian(1.0f / 7.0f);
		filterCombineSlide = new FilterCombineSlide();

		blurFactor = 0.0f;
	}

	@Override
	public void renderPipeline(int... textures) {
		// pipelineGaussian2.setScale(1.25f);
		pipelineGaussian1.renderPipeline(textures);

		pipelineGaussian2.setScale(1.25f);
		pipelineGaussian2.renderPipeline(pipelineGaussian1.getOutput().getColourTexture(0));

		filterDarken.setFactorValue(Math.max(Math.abs(1.0f - blurFactor), 0.45f));
		filterDarken.applyFilter(pipelineGaussian2.getOutput().getColourTexture(0));

		filterCombineSlide.setSlideSpace(blurFactor, 1.0f, 0.0f, 1.0f);
		filterCombineSlide.applyFilter(
				textures[0], // Colour
				filterDarken.fbo.getColourTexture(0)); // Darken
	}

	@Override
	public FBO getOutput() {
		return filterCombineSlide.fbo;
	}

	public void setBlurFactor(float blurFactor) {
		this.blurFactor = blurFactor;
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
		filterDarken.dispose();
		pipelineGaussian1.dispose();
		pipelineGaussian2.dispose();
		filterCombineSlide.dispose();
	}
}
