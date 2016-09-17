package flounder.devices;

import flounder.engine.*;
import flounder.maths.*;
import org.lwjgl.glfw.*;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Manages the creation, updating and destruction of the mouse.
 */
public class DeviceMouse implements IModule {
	private int mouseButtons[];
	private float lastMousePositionX;
	private float lastMousePositionY;
	private float mousePositionX;
	private float mousePositionY;
	private float mouseDeltaX;
	private float mouseDeltaY;
	private float mouseWheel;
	private boolean displaySelected;

	private GLFWScrollCallback callbackScroll;
	private GLFWMouseButtonCallback callbackMouseButton;
	private GLFWCursorPosCallback callbackCursorPos;
	private GLFWCursorEnterCallback callbackCursorEnter;

	/**
	 * Creates a new GLFW mouse.
	 */
	protected DeviceMouse() {
		mouseButtons = new int[GLFW_MOUSE_BUTTON_LAST];
		displaySelected = true;
		mousePositionX = 0.5f;
		mousePositionY = 0.5f;
	}

	@Override
	public void init() {
		// Sets the mouse callbacks.
		glfwSetScrollCallback(FlounderEngine.getDevices().getDisplay().getWindow(), callbackScroll = new GLFWScrollCallback() {
			@Override
			public void invoke(long window, double xoffset, double yoffset) {
				mouseWheel = (float) yoffset;
			}
		});

		glfwSetMouseButtonCallback(FlounderEngine.getDevices().getDisplay().getWindow(), callbackMouseButton = new GLFWMouseButtonCallback() {
			@Override
			public void invoke(long window, int button, int action, int mods) {
				mouseButtons[button] = action;
			}
		});

		glfwSetCursorPosCallback(FlounderEngine.getDevices().getDisplay().getWindow(), callbackCursorPos = new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double xpos, double ypos) {
				mousePositionX = (float) (xpos / FlounderEngine.getDevices().getDisplay().getWidth());
				mousePositionY = (float) (ypos / FlounderEngine.getDevices().getDisplay().getHeight());
			}
		});

		glfwSetCursorEnterCallback(FlounderEngine.getDevices().getDisplay().getWindow(), callbackCursorEnter = new GLFWCursorEnterCallback() {
			@Override
			public void invoke(long window, boolean entered) {
				displaySelected = entered;
			}
		});
	}

	@Override
	public void update() {
		// Gets the engines delta.
		float delta = FlounderEngine.getDelta();

		// Updates the mouses delta.
		mouseDeltaX = (lastMousePositionX - mousePositionX) * delta;
		mouseDeltaY = (lastMousePositionY - mousePositionY) * delta;

		// Sets the last position of the current.
		lastMousePositionX = mousePositionX;
		lastMousePositionY = mousePositionY;

		// Updates the mouse wheel using a smooth scroll technique.
		if (mouseWheel != 0.0f) {
			mouseWheel -= (((mouseWheel < 0.0f) ? -1.0f : 1.0f) * delta);
			mouseWheel = Maths.deadband(0.1f, mouseWheel);
		}
	}

	@Override
	public void profile() {

	}

	/**
	 * Sets if the operating systems cursor is hidden whilst in the display.
	 *
	 * @param disabled If the system cursor should be disabled or hidden when not shown.
	 */
	public void setCursorHidden(boolean disabled) {
		glfwSetInputMode(FlounderEngine.getDevices().getDisplay().getWindow(), GLFW_CURSOR, (disabled ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_HIDDEN));
	}

	/**
	 * Gets whether or not a particular mouse button is currently pressed.
	 * <p>GLFW Actions: GLFW_PRESS, GLFW_RELEASE, GLFW_REPEAT</p>
	 *
	 * @param button The mouse button to test.
	 *
	 * @return If the mouse button is currently pressed.
	 */
	public boolean getMouse(int button) {
		return mouseButtons[button] != GLFW_RELEASE;
	}

	/**
	 * Gets the mouses screen x position.
	 *
	 * @return The mouses x position.
	 */
	public float getPositionX() {
		return mousePositionX;
	}

	/**
	 * Gets the mouses screen y position.
	 *
	 * @return The mouses y position.
	 */
	public float getPositionY() {
		return mousePositionY;
	}

	/**
	 * Gets the mouses delta x.
	 *
	 * @return The mouses delta x.
	 */
	public float getDeltaX() {
		return mouseDeltaX;
	}

	/**
	 * Gets the mouses delta y.
	 *
	 * @return The mouses delta y.
	 */
	public float getDeltaY() {
		return mouseDeltaY;
	}

	/**
	 * Gets the mouses wheel delta.
	 *
	 * @return The mouses wheel delta.
	 */
	public float getDeltaWheel() {
		return mouseWheel;
	}

	/**
	 * Gets if the display is selected.
	 *
	 * @return If the display is selected.
	 */
	public boolean isDisplaySelected() {
		return displaySelected;
	}

	@Override
	public void dispose() {
		callbackScroll.free();
		callbackMouseButton.free();
		callbackCursorPos.free();
		callbackCursorEnter.free();
	}
}
