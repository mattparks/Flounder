package flounder.renderer;

/**
 * The engines main renderer, it organizes render objects passes of subtract renderer's.
 */
public interface IRendererMaster {
	/**
	 * Initializes the various renderer types and various functionality's.
	 */
	void init();

	/**
	 * Carries out the rendering of all components.
	 */
	void render();

	/**
	 * Profiles the module.
	 */
	void profile();

	/**
	 * Gets if the renderer is currently active, will disable the renderer if false.
	 *
	 * @return If the renderer is currently active.
	 */
	boolean isActive();

	/**
	 * Cleans up all of the render objects processes. Should be called when the game closes.
	 */
	void dispose();
}
