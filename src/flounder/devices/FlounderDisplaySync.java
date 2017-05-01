package flounder.devices;

import flounder.framework.*;

/**
 * A module used for synchronizing to the display after rendering.
 */
public class FlounderDisplaySync extends Module {
	/**
	 * Creates a new GLFW display synchronizer.
	 */
	public FlounderDisplaySync() {
		super(FlounderDisplay.class);
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
	}

	@Handler.Function(Handler.FLAG_RENDER)
	public void update() {
		FlounderDisplay.get().swapBuffers();
	}

	@Handler.Function(Handler.FLAG_PROFILE)
	public void profile() {
	}


	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
	}

	@Module.Instance
	public static FlounderDisplaySync get() {
		return (FlounderDisplaySync) Framework.getInstance(FlounderDisplaySync.class);
	}

	@Module.TabName
	public static String getTab() {
		return "Display-Sync";
	}
}
