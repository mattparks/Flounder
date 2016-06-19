package flounder.post.piplines;

import flounder.fbos.*;
import flounder.post.*;
import flounder.post.filters.*;

public class PipelineDepthOfField extends PostPipeline {
	private static final int BLUR_TEXTURE_WIDTH = 256;
	private static final int BLUR_TEXTURE_HEIGHT = 144;

	private PipelineGaussian pipelineGaussian;
	private FilterFXAA filterFXAA;
	private FilterDOF filterDOF;

	public PipelineDepthOfField() {
		pipelineGaussian = new PipelineGaussian(BLUR_TEXTURE_WIDTH, BLUR_TEXTURE_HEIGHT, false);
		filterFXAA = new FilterFXAA();
		filterDOF = new FilterDOF();
	}

	@Override
	public void renderPipeline(FBO startFBO) {
		filterFXAA.applyFilter(startFBO.getColourTexture());
		pipelineGaussian.setScale(0.5f);
		pipelineGaussian.renderPipeline(filterFXAA.fbo);
		filterDOF.applyFilter(filterFXAA.fbo.getColourTexture(), startFBO.getDepthTexture(), pipelineGaussian.getOutput().getColourTexture());
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
