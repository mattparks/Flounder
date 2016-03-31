package flounder.devices;

import org.lwjgl.glfw.*;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Manages the creation, updating and destruction of the keyboard keys.
 */
public class DeviceKeyboard {
	private final GLFWKeyCallback callbackKey;

	private int m_keys[];

	/**
	 * Creates a new GLFW keyboard.
	 */
	protected DeviceKeyboard() {
		m_keys = new int[GLFW_KEY_LAST + 1];

		// Sets the keyboards callbacks.
		glfwSetKeyCallback(ManagerDevices.getDisplay().getWindow(), callbackKey = new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if (key < 0 || key > GLFW_KEY_LAST) {
					System.err.println("Invalid action attempted with key: " + key);
				} else {
					// Actions: GLFW_PRESS   GLFW_RELEASE   GLFW_REPEAT
					m_keys[key] = action;
				}
			}
		});
	}

	/**
	 * Updates the keyboard system. Should be called once every frame.
	 *
	 * @param delta The time in seconds since the last frame.
	 */
	protected void update(final float delta) {
	}

	/**
	 * Gets whether or not a particular key is currently pressed.
	 *
	 * @param key The key to test.
	 *
	 * @return If the key is currently pressed.
	 */
	public boolean getKey(final int key) {
		return m_keys[key] != GLFW_RELEASE;
	}

	/**
	 * Closes the GLFW keyboard system, do not use the keyboard after calling this.
	 */
	protected void dispose() {
		callbackKey.release();
	}
}
