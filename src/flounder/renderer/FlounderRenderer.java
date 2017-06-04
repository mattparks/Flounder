package flounder.renderer;

import flounder.camera.*;
import flounder.devices.*;
import flounder.framework.*;
import flounder.shaders.*;

/**
 * A module used for OpenGL rendering and management.
 */
public class FlounderRenderer extends Module {
	private RendererMaster renderer;

	/**
	 * Creates a new OpenGL renderer manager.
	 */
	public FlounderRenderer() {
		super(FlounderDisplay.class, FlounderCamera.class, FlounderShaders.class);
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
		this.renderer = null;
	}

	@Handler.Function(Handler.FLAG_RENDER)
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

	/**
	 * Gets the current renderer extension.
	 *
	 * @return The current renderer.
	 */
	public RendererMaster getRendererMaster() {
		return this.renderer;
	}


	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
		// Disposes the renderer with the module.
		if (renderer != null) {
			renderer.dispose();
			renderer.setInitialized(false);
		}
	}

	@Module.Instance
	public static FlounderRenderer get() {
		return (FlounderRenderer) Framework.get().getInstance(FlounderRenderer.class);
	}
}
