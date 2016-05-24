package flounder.post.piplines;

import flounder.post.*;
import flounder.post.filters.*;
import flounder.textures.fbos.*;

public class PipelineGaussian extends PostPipeline {
	private final FilterBlurHorizontal filterBlurHorizontal;
	private final FilterBlurVertical filterBlurVertical;

	public PipelineGaussian(final int width, final int height, final boolean fitToDisplay) {
		filterBlurHorizontal = fitToDisplay ? new FilterBlurHorizontal() : new FilterBlurHorizontal(width, height);
		filterBlurVertical = fitToDisplay ? new FilterBlurVertical() : new FilterBlurVertical(width, height);
	}

	@Override
	public void renderPipeline(final FBO startFBO) {
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

	public void setScale(final float scale) {
		filterBlurHorizontal.setScale(scale);
		filterBlurVertical.setScale(scale);
	}
}
