package flounder.engine;

import flounder.maths.matrices.*;

/**
 * The engines main renderer, it organizes render objects passes of subtract renderer's.
 */
public abstract class IRendererMaster {
	/**
	 * Initializes the various renderer types and various functionality's.
	 */
	public abstract void init();

	/**
	 * Carries out the rendering of all components.
	 */
	public abstract void render();

	/**
	 * @return The projection matrix used in the current scene renderObjects.
	 */
	public abstract Matrix4f getProjectionMatrix();

	/**
	 * Cleans up all of the render objects processes. Should be called when the game closes.
	 */
	public abstract void dispose();
}
