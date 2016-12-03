package flounder.devices;

import flounder.framework.*;

/**
 * A module used for synchronizing to the display after rendering.
 */
public class FlounderDisplaySync extends IModule {
	private static final FlounderDisplaySync instance = new FlounderDisplaySync();

	/**
	 * Creates a new GLFW display synchronizer.
	 */
	public FlounderDisplaySync() {
		super(ModuleUpdate.RENDER, FlounderDisplay.class);
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
	public IModule getInstance() {
		return instance;
	}

	@Override
	public void dispose() {
	}
}
