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

	@Override
	public void init() {
	}

	@Override
	public void update() {
		FlounderDisplay.swapBuffers();
	}

	@Override
	public void profile() {
	}

	@Override
	public Module getInstance() {
		return INSTANCE;
	}

	@Override
	public void dispose() {
	}
}
