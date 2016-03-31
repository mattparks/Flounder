package flounder.engine;

import flounder.engine.profiling.*;
import flounder.maths.vectors.*;

/**
 * Represents a sub renderer in the engine.
 */
public abstract class IRenderer {
	private final ProfileTimer m_profileTimer;
	private float m_renderTimeMs;

	/**
	 * Creates a new sub renderer in the engine.
	 */
	public IRenderer() {
		m_profileTimer = new ProfileTimer();
		m_renderTimeMs = 0.0f;
	}

	/**
	 * Called when the renderer is needed to be rendered.
	 *
	 * @param clipPlane The current clip plane.
	 * @param camera The camera to be used when rendering.
	 */
	public void render(final Vector4f clipPlane, final ICamera camera) {
		m_profileTimer.startInvocation();
		renderObjects(clipPlane, camera);
		m_profileTimer.stopInvocation();
		m_renderTimeMs = m_profileTimer.reset();
	}

	/**
	 * @return The last render time (in Ms).
	 */
	public float getRenderTimeMs() {
		return m_renderTimeMs;
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
