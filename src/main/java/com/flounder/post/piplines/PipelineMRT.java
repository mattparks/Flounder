package com.flounder.post.piplines;

import com.flounder.devices.*;
import com.flounder.fbos.*;
import com.flounder.post.*;
import com.flounder.post.filters.*;

public class PipelineMRT extends PostPipeline {
	private FilterMRT filterMRT;
	private FilterFXAA filterFXAA;
	private FBO result;

	public PipelineMRT() {
		this.filterMRT = new FilterMRT();
		this.filterFXAA = new FilterFXAA();
	}

	@Override
	public void renderPipeline(int... textures) {
		// Texture data used in filter:
		// textures[0], // Colours
		// textures[1], // Normals
		// textures[2], // Extras
		// textures[3], // Depth
		// textures[4], // Shadow Map
		filterMRT.applyFilter(textures);
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

	@Override
	public void dispose() {
		filterMRT.dispose();
		filterFXAA.dispose();
	}
}
