package flounder.devices;

import flounder.framework.*;
import flounder.logger.*;
import org.lwjgl.glfw.*;

import static org.lwjgl.glfw.GLFW.*;

/**
 * A module used for the creation, updating and destruction of the keyboard keys.
 */
public class FlounderKeyboard extends Module {
	private static final FlounderKeyboard INSTANCE = new FlounderKeyboard();
	public static final String PROFILE_TAB_NAME = "Keyboard";

	private int keyboardKeys[];
	private int keyboardChar;

	private GLFWKeyCallback callbackKey;
	private GLFWCharCallback callbackChar;

	/**
	 * Creates a new GLFW keyboard manager.
	 */
	public FlounderKeyboard() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME, FlounderLogger.class, FlounderDisplay.class);
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
		this.keyboardKeys = new int[GLFW_KEY_LAST + 1];
		this.keyboardChar = 0;

		// Sets the keyboards callbacks.
		glfwSetKeyCallback(FlounderDisplay.getWindow(), callbackKey = new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if (key < 0 || key > GLFW_KEY_LAST) {
					FlounderLogger.get().error("Invalid action attempted with key " + key);
				} else {
					keyboardKeys[key] = action;
				}
			}
		});

		glfwSetCharCallback(FlounderDisplay.getWindow(), callbackChar = new GLFWCharCallback() {
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
	public static boolean getKey(int key) {
		return INSTANCE.keyboardKeys[key] != GLFW_RELEASE;
	}

	/**
	 * Gets the current user input, ASCII Dec value.
	 *
	 * @return The current keyboard char.
	 */
	public static int getKeyboardChar() {
		return INSTANCE.keyboardChar;
	}


	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
		callbackKey.free();
		callbackChar.free();
	}
}
