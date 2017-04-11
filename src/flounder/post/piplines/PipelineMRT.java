package flounder.post.piplines;

import flounder.devices.*;
import flounder.fbos.*;
import flounder.post.*;
import flounder.post.filters.*;

public class PipelineMRT extends PostPipeline {
	private FilterMRT filterMRT;
	private FilterFXAA filterFXAA;
	private boolean runFXAA;

	public PipelineMRT() {
		this.filterMRT = new FilterMRT();
		this.filterFXAA = new FilterFXAA();
	}

	@Override
	public void renderPipeline(int... textures) {
		runFXAA = FlounderDisplay.isAntialiasing();

		// Texture data used in filter:
		// textures[0], // Colours
		// textures[1], // Normals
		// textures[2], // Extras
		// textures[3], // Depth
		// textures[4], // Shadow Map
		filterMRT.applyFilter(textures);

		if (runFXAA) {
			filterFXAA.applyFilter(filterMRT.fbo.getColourTexture(0));
		}
	}

	@Override
	public FBO getOutput() {
		if (runFXAA) {
			return filterFXAA.fbo;
		} else {
			return filterMRT.fbo;
		}
	}

	public void setShadowFactor(float shadowFactor) {
		filterMRT.setShadowFactor(shadowFactor);
	}

	@Override
	public void dispose() {
		filterMRT.dispose();
		filterFXAA.dispose();
	}
}
