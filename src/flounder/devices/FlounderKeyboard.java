package flounder.devices;

import flounder.framework.*;
import flounder.logger.*;
import flounder.platform.*;
import org.lwjgl.glfw.*;

import static org.lwjgl.glfw.GLFW.*;

/**
 * A module used for the creation, updating and destruction of the keyboard keys.
 */
public class FlounderKeyboard extends Module {
	private int keyboardKeys[];
	private int keyboardChar;

	private GLFWKeyCallback callbackKey;
	private GLFWCharCallback callbackChar;

	/**
	 * Creates a new GLFW keyboard manager.
	 */
	public FlounderKeyboard() {
		super(FlounderPlatform.class, FlounderLogger.class, FlounderDisplay.class);
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
		this.keyboardKeys = new int[GLFW_KEY_LAST + 1];
		this.keyboardChar = 0;

		// Sets the keyboards callbacks.
		glfwSetKeyCallback(FlounderDisplay.get().getWindow(), callbackKey = new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if (key < 0 || key > GLFW_KEY_LAST) {
					FlounderLogger.get().error("Invalid action attempted with key " + key);
				} else {
					keyboardKeys[key] = action;
				}
			}
		});

		glfwSetCharCallback(FlounderDisplay.get().getWindow(), callbackChar = new GLFWCharCallback() {
			@Override
			public void invoke(long window, int codepoint) {
				keyboardChar = codepoint;
			}
		});
	}

	@Handler.Function(Handler.FLAG_UPDATE_PRE)
	public void update() {
	}

	@Handler.Function(Handler.FLAG_PROFILE)
	public void profile() {
	}

	/**
	 * Gets whether or not a particular key is currently pressed.
	 * <p>GLFW Actions: GLFW_PRESS, GLFW_RELEASE, GLFW_REPEAT</p>
	 *
	 * @param key The key to test.
	 *
	 * @return If the key is currently pressed.
	 */
	public boolean getKey(int key) {
		return this.keyboardKeys[key] != GLFW_RELEASE;
	}

	/**
	 * Gets the current user input, ASCII Dec value.
	 *
	 * @return The current keyboard char.
	 */
	public int getKeyboardChar() {
		return this.keyboardChar;
	}


	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
		callbackKey.free();
		callbackChar.free();
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
