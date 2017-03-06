package flounder.post.piplines;

import flounder.fbos.*;
import flounder.post.*;
import flounder.post.filters.*;

public class PipelineBloom extends PostPipeline {
	private FilterBloom1 filterBloom1;
	private PipelineGaussian pipelineGaussian;
	private FilterBloom2 filterBloom2;

	public PipelineBloom() {
		filterBloom1 = new FilterBloom1();
		pipelineGaussian = new PipelineGaussian(0.5f);
		filterBloom2 = new FilterBloom2();
	}

	public void renderMRT(FBO fboMRT, FBO fboColour) {
		filterBloom1.applyFilter(fboColour.getColourTexture(0));
		pipelineGaussian.renderPipeline(filterBloom1.fbo);
		filterBloom2.applyFilter(fboColour.getColourTexture(0), pipelineGaussian.getOutput().getColourTexture(0));
	}

	@Override
	public void renderPipeline(FBO startFBO) {
		filterBloom1.applyFilter(startFBO.getColourTexture(0));
		pipelineGaussian.renderPipeline(filterBloom1.fbo);
		filterBloom2.applyFilter(startFBO.getColourTexture(0), pipelineGaussian.getOutput().getColourTexture(0));
	}

	@Override
	public FBO getOutput() {
		return filterBloom2.fbo;
	}

	public void setBloomThreshold(float bloomThreshold) {
		this.filterBloom1.setBloomThreshold(bloomThreshold);
	}

	@Override
	public void dispose() {
		filterBloom1.dispose();
		pipelineGaussian.dispose();
		filterBloom2.dispose();
	}
}
