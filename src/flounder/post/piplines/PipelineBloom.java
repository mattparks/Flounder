package flounder.post.piplines;

import flounder.post.*;
import flounder.post.filters.*;
import flounder.textures.fbos.*;

public class PipelineBloom extends PostPipeline {
	private static final int BLUR_TEXTURE_WIDTH = 256;
	private static final int BLUR_TEXTURE_HEIGHT = 144;

	private final FilterFXAA filterFXAA;
	private final FilterTone filterTone;
	private final FilterBloom1 filterBloom1;
	private final PipelineGaussian pipelineGaussian;
	private final FilterBloom2 filterBloom2;

	public PipelineBloom() {
		filterFXAA = new FilterFXAA();
		filterTone = new FilterTone();
		filterBloom1 = new FilterBloom1();
		pipelineGaussian = new PipelineGaussian(BLUR_TEXTURE_WIDTH, BLUR_TEXTURE_HEIGHT, false);
		filterBloom2 = new FilterBloom2();
	}

	@Override
	public void renderPipeline(final FBO startFBO) {
		filterFXAA.applyFilter(startFBO.getColourTexture());
		filterTone.applyFilter(filterFXAA.fbo.getColourTexture());
		filterBloom1.applyFilter(filterTone.fbo.getColourTexture());
		pipelineGaussian.setScale(0.5f);
		pipelineGaussian.renderPipeline(filterBloom1.fbo);
		filterBloom2.applyFilter(filterTone.fbo.getColourTexture(), pipelineGaussian.getOutput().getColourTexture());
	}

	@Override
	public FBO getOutput() {
		return filterBloom2.fbo;
	}

	@Override
	public void dispose() {
		filterFXAA.dispose();
		filterTone.dispose();
		filterBloom1.dispose();
		pipelineGaussian.dispose();
		filterBloom2.dispose();
	}
}
