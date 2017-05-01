package flounder.devices;

import flounder.framework.*;

/**
 * A module used for synchronizing to the display after rendering.
 */
public class FlounderDisplaySync extends Module {
	private static final FlounderDisplaySync INSTANCE = new FlounderDisplaySync();
	public static final String PROFILE_TAB_NAME = "Display-Sync";

	/**
	 * Creates a new GLFW display synchronizer.
	 */
	public FlounderDisplaySync() {
		super(ModuleUpdate.UPDATE_RENDER, PROFILE_TAB_NAME, FlounderDisplay.class);
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
	}

	@Handler.Function(Handler.FLAG_UPDATE_PRE)
	public void update() {
		FlounderDisplay.swapBuffers();
	}

	@Handler.Function(Handler.FLAG_PROFILE)
	public void profile() {
	}


	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
	}
}
