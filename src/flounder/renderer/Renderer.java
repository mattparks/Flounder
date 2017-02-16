package flounder.renderer;

import flounder.camera.*;
import flounder.maths.vectors.*;
import flounder.profiling.*;

/**
 * Represents a sub renderer in the engine.
 */
public abstract class Renderer {
	private ProfileTimer profileTimer;

	/**
	 * Creates a new sub renderer in the engine.
	 */
	public Renderer() {
		// TODO: Have own internal Module requirements to render.
		profileTimer = new ProfileTimer();
	}

	/**
	 * Called when the renderer is needed to be rendered.
	 *
	 * @param clipPlane The current clip plane.
	 * @param camera The camera to be used when rendering.
	 */
	public void render(Vector4f clipPlane, Camera camera) {
		profileTimer.startInvocation();
		renderObjects(clipPlane, camera);
		profileTimer.stopInvocation();
		profileTimer.reset();

		if (FlounderProfiler.isOpen()) {
			// TODO: Limit profile speed.
			profile();
		}
	}

	/**
	 * An internal render method for renderers.
	 *
	 * @param clipPlane The current clip plane.
	 * @param camera The camera to be used when rendering.
	 */
	public abstract void renderObjects(Vector4f clipPlane, Camera camera);

	/**
	 * A internal render method for profiling render times and other values.
	 */
	public abstract void profile();

	/**
	 * @return The last render time (in seconds).
	 */
	public double getRenderTime() {
		return profileTimer.getFinalTime();
	}

	/**
	 * Cleans up all of the renderers processes.
	 */
	public abstract void dispose();
}
