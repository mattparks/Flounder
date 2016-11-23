package flounder.devices;

import flounder.framework.*;

/**
 * A class that is used to sync to the display after rendering.
 */
public class FlounderDisplaySync extends IModule {
	private static final FlounderDisplaySync instance = new FlounderDisplaySync();

	public FlounderDisplaySync() {
		super(ModuleUpdate.RENDER, FlounderDisplay.class);
	}

	@Override
	public void init() {
	}

	@Override
	public void run() {
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
