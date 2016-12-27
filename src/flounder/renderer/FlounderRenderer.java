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
		IRendererMaster newRenderer = (IRendererMaster) FlounderModules.getExtensionMatch(getInstance(), renderer, true);

		if (newRenderer != null) {
			if (renderer != null) {
				renderer.dispose();
				renderer.setInitialized(false);
			}

			renderer = newRenderer;
		}

		if (renderer != null) {
			if (!renderer.isInitialized()) {
				renderer.init();
				renderer.setInitialized(true);
			}

			renderer.render();
		}
	}

	@Override
	public void profile() {
		FlounderProfiler.add("Renderer", "Selected", renderer == null ? "NULL" : renderer.getClass());
	}

	public static IRendererMaster getRendererMaster() {
		return instance.renderer;
	}

	@Override
	public IModule getInstance() {
		return instance;
	}

	@Override
	public void dispose() {
		if (renderer != null) {
			renderer.dispose();
			renderer.setInitialized(false);
		}
	}
}
