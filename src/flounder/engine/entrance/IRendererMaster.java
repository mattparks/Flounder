package flounder.engine.entrance;

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
	 * Cleans up all of the render objects processes. Should be called when the game closes.
	 */
	public abstract void dispose();
}
