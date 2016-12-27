package flounder.renderer;

import flounder.camera.*;
import flounder.maths.vectors.*;
import flounder.profiling.*;

/**
 * Represents a sub renderer in the engine.
 */
public abstract class IRenderer {
	private ProfileTimer profileTimer;
	private float renderTimeMs;

	/**
	 * Creates a new sub renderer in the engine.
	 */
	public IRenderer() {
		// TODO: Have own internal Module requirements to render.
		profileTimer = new ProfileTimer();
		renderTimeMs = 0.0f;
	}

	/**
	 * Called when the renderer is needed to be rendered.
	 *
	 * @param clipPlane The current clip plane.
	 * @param camera The camera to be used when rendering.
	 */
	public void render(Vector4f clipPlane, ICamera camera) {
		profileTimer.startInvocation();
		renderObjects(clipPlane, camera);
		profileTimer.stopInvocation();
		renderTimeMs = profileTimer.reset();

		if (FlounderProfiler.isOpen()) {
			profile();
		}
	}

	/**
	 * An internal render method for renderers.
	 *
	 * @param clipPlane The current clip plane.
	 * @param camera The camera to be used when rendering.
	 */
	public abstract void renderObjects(Vector4f clipPlane, ICamera camera);

	/**
	 * A internal render method for profiling render times and other values.
	 */
	public abstract void profile();

	/**
	 * @return The last render time (in Ms).
	 */
	public float getRenderTimeMs() {
		return renderTimeMs;
	}

	/**
	 * Cleans up all of the renderers processes.
	 */
	public abstract void dispose();
}
