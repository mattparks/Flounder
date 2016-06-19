package flounder.post;

import flounder.fbos.*;

/**
 * Represents a system of post effects.
 */
public abstract class PostPipeline {
	/**
	 * Renders the post pipeline.
	 *
	 * @param startFBO The original screen FBO.
	 */
	public abstract void renderPipeline(FBO startFBO);

	/**
	 * @return Returns the FBO containing the result.
	 */
	public abstract FBO getOutput();

	/**
	 * Cleans up all of the pipelines filters and processes.
	 */
	public abstract void dispose();
}
