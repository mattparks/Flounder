package flounder.renderer;

import flounder.camera.*;
import flounder.devices.*;
import flounder.framework.*;
import flounder.profiling.*;
import flounder.shaders.*;

/**
 * A module used for OpenGL rendering and management.
 */
public class FlounderRenderer extends Module {
	private static final FlounderRenderer INSTANCE = new FlounderRenderer();
	public static final String PROFILE_TAB_NAME = "Renderer";

	private RendererMaster renderer;

	/**
	 * Creates a new OpenGL renderer manager.
	 */
	public FlounderRenderer() {
		super(ModuleUpdate.UPDATE_RENDER, PROFILE_TAB_NAME, FlounderDisplay.class, FlounderCamera.class, FlounderShaders.class);
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
		this.renderer = null;
	}

	@Handler.Function(Handler.FLAG_UPDATE_PRE)
	public void update() {
		// Gets a new renderer, if available.
		RendererMaster newRenderer = (RendererMaster) getExtensionMatch(renderer, RendererMaster.class, true);

		// If there is a new renderer, disable the old one and start to use the new one.
		if (newRenderer != null) {
			if (renderer != null) {
				renderer.dispose();
				renderer.setInitialized(false);
			}

			if (!newRenderer.isInitialized()) {
				newRenderer.init();
				newRenderer.setInitialized(true);
			}

			renderer = newRenderer;
		}

		// Runs updates for the renderer.
		if (renderer != null) {
			renderer.render();
		}
	}

	@Handler.Function(Handler.FLAG_PROFILE)
	public void profile() {
		FlounderProfiler.get().add(PROFILE_TAB_NAME, "Selected", renderer == null ? "NULL" : renderer.getClass());
	}

	/**
	 * Gets the current renderer extension.
	 *
	 * @return The current renderer.
	 */
	public static RendererMaster getRendererMaster() {
		return INSTANCE.renderer;
	}


	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
		// Disposes the renderer with the module.
		if (renderer != null) {
			renderer.dispose();
			renderer.setInitialized(false);
		}
	}
}
