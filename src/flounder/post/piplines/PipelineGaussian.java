package flounder.post.piplines;

import flounder.fbos.*;
import flounder.post.*;
import flounder.post.filters.*;

public class PipelineGaussian extends PostPipeline {
	private FilterBlurHorizontal filterBlurHorizontal;
	private FilterBlurVertical filterBlurVertical;

	public PipelineGaussian(int width, int height, boolean fitToDisplay) {
		filterBlurHorizontal = fitToDisplay ? new FilterBlurHorizontal() : new FilterBlurHorizontal(width, height);
		filterBlurVertical = fitToDisplay ? new FilterBlurVertical() : new FilterBlurVertical(width, height);
	}

	@Override
	public void renderPipeline(FBO startFBO) {
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

	public void setScale(float scale) {
		filterBlurHorizontal.setScale(scale);
		filterBlurVertical.setScale(scale);
	}
}
