package com.flounder.devices;

import com.flounder.framework.*;
import com.flounder.platform.*;

/**
 * A module used for synchronizing to the display after rendering.
 */
public class FlounderDisplaySync extends com.flounder.framework.Module {
	/**
	 * Creates a new GLFW display synchronizer.
	 */
	public FlounderDisplaySync() {
		super(FlounderPlatform.class, FlounderDisplay.class);
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
	}

	@Handler.Function(Handler.FLAG_RENDER)
	public void update() {
		FlounderDisplay.get().swapBuffers();
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
	}

	@com.flounder.framework.Module.Instance
	public static FlounderDisplaySync get() {
		return (FlounderDisplaySync) Framework.get().getModule(FlounderDisplaySync.class);
	}
}
