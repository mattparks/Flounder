package flounder.post.piplines;

import flounder.engine.options.*;
import flounder.post.*;
import flounder.post.filters.*;
import flounder.textures.fbos.*;

public class PipelineDemo extends PostPipeline {
	private final FilterEmboss filterEmboss;
	private final FilterGray filterGray;
	private final FilterNegative filterNegative;
	private final FilterPixel filterPixel;
	private final FilterSepia filterSepia;
	private final FilterTone filterTone;
	private final FilterWobble filterWobble;
	private final FilterFXAA filterFXAA;

	public PipelineDemo() {
		filterEmboss = new FilterEmboss();
		filterGray = new FilterGray();
		filterNegative = new FilterNegative();
		filterPixel = new FilterPixel();
		filterSepia = new FilterSepia();
		filterTone = new FilterTone();
		filterWobble = new FilterWobble();
		filterFXAA = new FilterFXAA();
	}

	@Override
	public void renderPipeline(final FBO startFBO) {
		switch (OptionsGraphics.POST_EFFECT) {
			case 1:
				filterEmboss.applyFilter(startFBO.getColourTexture());
				filterFXAA.applyFilter(filterEmboss.fbo.getColourTexture());
				break;
			case 2:
				filterGray.applyFilter(startFBO.getColourTexture());
				filterFXAA.applyFilter(filterGray.fbo.getColourTexture());
				break;
			case 3:
				filterNegative.applyFilter(startFBO.getColourTexture());
				filterFXAA.applyFilter(filterNegative.fbo.getColourTexture());
				break;
			case 4:
				filterPixel.applyFilter(startFBO.getColourTexture());
				filterFXAA.applyFilter(filterPixel.fbo.getColourTexture());
				break;
			case 5:
				filterSepia.applyFilter(startFBO.getColourTexture());
				filterFXAA.applyFilter(filterSepia.fbo.getColourTexture());
				break;
			case 6:
				filterTone.applyFilter(startFBO.getColourTexture());
				filterFXAA.applyFilter(filterTone.fbo.getColourTexture());
				break;
			case 7:
				filterWobble.applyFilter(startFBO.getColourTexture());
				filterFXAA.applyFilter(filterWobble.fbo.getColourTexture());
				break;
			default:
				filterFXAA.applyFilter(startFBO.getColourTexture());
				break;
		}
	}

	@Override
	public FBO getOutput() {
		return filterFXAA.fbo;
	}

	@Override
	public void dispose() {
		filterEmboss.dispose();
		filterGray.dispose();
		filterNegative.dispose();
		filterPixel.dispose();
		filterSepia.dispose();
		filterTone.dispose();
		filterWobble.dispose();
		filterFXAA.dispose();
	}
}
