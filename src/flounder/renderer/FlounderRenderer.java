package flounder.renderer;

import flounder.devices.*;
import flounder.framework.*;
import flounder.logger.*;
import flounder.profiling.*;

/**
 * A module used for OpenGL rendering and management.
 */
public class FlounderRenderer extends IModule {
	private static final FlounderRenderer instance = new FlounderRenderer();

	private IRendererMaster renderer;

	/**
	 * Creates a new OpenGL renderer manager.
	 */
	public FlounderRenderer() {
		super(ModuleUpdate.UPDATE_RENDER, FlounderLogger.class, FlounderProfiler.class, FlounderDisplay.class);
	}

	@Override
	public void init() {
		this.renderer = null;
	}

	@Override
	public void update() {
		// Gets a new renderer, if available.
		IRendererMaster newRenderer = (IRendererMaster) getExtensionMatch(renderer, IRendererMaster.class, true);

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

	@Override
	public void profile() {
		FlounderProfiler.add("Renderer", "Selected", renderer == null ? "NULL" : renderer.getClass());
	}

	/**
	 * Gets the current renderer extension.
	 *
	 * @return The current renderer.
	 */
	public static IRendererMaster getRendererMaster() {
		return instance.renderer;
	}

	@Override
	public IModule getInstance() {
		return instance;
	}

	@Override
	public void dispose() {
		// Disposes the renderer with the module.
		if (renderer != null) {
			renderer.dispose();
			renderer.setInitialized(false);
		}
	}
}
