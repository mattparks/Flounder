package flounder.post.piplines;

import flounder.devices.*;
import flounder.fbos.*;
import flounder.helpers.*;
import flounder.post.*;
import flounder.post.filters.*;

public class PipelineMRT extends PostPipeline {
	private FilterSSAO filterSSAO;
	private FilterMRT filterMRT;
	private FilterFXAA filterFXAA;
	private FBO result;

	public PipelineMRT() {
		this.filterSSAO = new FilterSSAO();
		this.filterMRT = new FilterMRT();
		this.filterFXAA = new FilterFXAA();
	}

	@Override
	public void renderPipeline(int... textures) {
		filterSSAO.applyFilter(textures);

		// Texture data used in filter:
		// textures[0], // Colours
		// textures[1], // Normals
		// textures[2], // Extras
		// textures[3], // Depth
		// textures[4], // Shadow Map
		// textures[5], // SSAO Buffer
		filterMRT.applyFilter(ArrayUtils.addElement(textures, filterSSAO.fbo.getColourTexture(0)));
		result = filterMRT.fbo;

		if (FlounderDisplay.get().isAntialiasing()) {
			filterFXAA.applyFilter(filterMRT.fbo.getColourTexture(0));
			result = filterFXAA.fbo;
		}
	}

	@Override
	public FBO getOutput() {
		return result;
	}

	public void setShadowFactor(float shadowFactor) {
		filterMRT.setShadowFactor(shadowFactor);
	}

	@Override
	public void dispose() {
		filterSSAO.dispose();
		filterMRT.dispose();
		filterFXAA.dispose();
	}
}
