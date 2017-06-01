package flounder.devices;

import flounder.framework.*;
import flounder.logger.*;
import flounder.platform.*;

/**
 * A module used for the creation, updating and destruction of the keyboard keys.
 */
public class FlounderKeyboard extends Module {
	/**
	 * Creates a new GLFW keyboard manager.
	 */
	public FlounderKeyboard() {
		super(FlounderPlatform.class, FlounderLogger.class, FlounderDisplay.class);
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
	}

	@Handler.Function(Handler.FLAG_UPDATE_PRE)
	public void update() {
	}

	/**
	 * Gets whether or not a particular key is currently pressed.
	 * <p>GLFW Actions: GLFW_PRESS, GLFW_RELEASE, GLFW_REPEAT</p>
	 *
	 * @param key The key to test.
	 *
	 * @return If the key is currently pressed.
	 */
	@Module.MethodReplace
	public boolean getKey(int key) {
		return false;
	}

	/**
	 * Gets the current user input, ASCII Dec value.
	 *
	 * @return The current keyboard char.
	 */
	@Module.MethodReplace
	public int getKeyboardChar() {
		return 0;
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
	}

	@Module.Instance
	public static FlounderKeyboard get() {
		return (FlounderKeyboard) Framework.getInstance(FlounderKeyboard.class);
	}

	@Module.TabName
	public static String getTab() {
		return "Keyboard";
	}
}
