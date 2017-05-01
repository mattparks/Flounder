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

	@Override
	public void renderPipeline(int... textures) {
		filterBloom1.applyFilter(textures);
		pipelineGaussian.renderPipeline(filterBloom1.fbo.getColourTexture(0));
		filterBloom2.applyFilter(
				textures[0], // Colour
				pipelineGaussian.getOutput().getColourTexture(0) // Blurred
		);
	}

	@Override
	public FBO getOutput() {
		return filterBloom2.fbo;
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
		filterBloom1.dispose();
		pipelineGaussian.dispose();
		filterBloom2.dispose();
	}
}
