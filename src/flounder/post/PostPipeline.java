package flounder.post;

import flounder.fbos.*;

/**
 * Represents a system of post effects.
 */
public abstract class PostPipeline {
	/**
	 * Renders the post pipeline.
	 *
	 * @param textures A list of textures in indexed order to be bound for the shader program.
	 */
	public abstract void renderPipeline(int... textures);

	/**
	 * @return Returns the FBO containing the result.
	 */
	public abstract FBO getOutput();

	/**
	 * Cleans up all of the pipelines filters and processes.
	 */
	public abstract void dispose();
}
