package flounder.engine;

import flounder.engine.profiling.*;
import flounder.maths.vectors.*;

/**
 * Represents a sub renderer in the engine.
 */
public abstract class IRenderer {
	private final ProfileTimer profileTimer;
	private float renderTimeMs;

	/**
	 * Creates a new sub renderer in the engine.
	 */
	public IRenderer() {
		profileTimer = new ProfileTimer();
		renderTimeMs = 0.0f;
	}

	/**
	 * Called when the renderer is needed to be rendered.
	 *
	 * @param clipPlane The current clip plane.
	 * @param camera The camera to be used when rendering.
	 */
	public void render(final Vector4f clipPlane, final ICamera camera) {
		profileTimer.startInvocation();
		renderObjects(clipPlane, camera);
		profileTimer.stopInvocation();
		renderTimeMs = profileTimer.reset();
	}

	/**
	 * @return The last render time (in Ms).
	 */
	public float getRenderTimeMs() {
		return renderTimeMs;
	}

	/**
	 * An internal render method for Renderers.
	 *
	 * @param clipPlane The current clip plane.
	 * @param camera The camera to be used when rendering.
	 */
	public abstract void renderObjects(final Vector4f clipPlane, final ICamera camera);

	/**
	 * Cleans up all of the renderers processes.
	 */
	public abstract void dispose();
}
