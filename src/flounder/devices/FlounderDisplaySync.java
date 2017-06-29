package flounder.devices;

import flounder.framework.*;
import flounder.platform.*;

/**
 * A module used for synchronizing to the display after rendering.
 */
public class FlounderDisplaySync extends Module {
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

	@Module.Instance
	public static FlounderDisplaySync get() {
		return (FlounderDisplaySync) Framework.get().getInstance(FlounderDisplaySync.class);
	}
}
