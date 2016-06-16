package flounder.devices;

import flounder.engine.*;
import flounder.maths.*;
import flounder.profiling.*;
import org.lwjgl.glfw.*;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Manages the creation, updating and destruction of the mouse.
 */
public class DeviceMouse {
	private GLFWScrollCallback callbackScroll;
	private GLFWMouseButtonCallback callbackMouseButton;
	private GLFWCursorPosCallback callbackCursorPos;
	private GLFWCursorEnterCallback callbackCursorEnter;

	private int mouseButtons[];
	private float lastMousePositionX;
	private float lastMousePositionY;
	private float mousePositionX;
	private float mousePositionY;
	private float mouseDeltaX;
	private float mouseDeltaY;
	private float mouseWheel;
	private boolean displaySelected;

	/**
	 * Creates a new GLFW mouse.
	 */
	protected DeviceMouse() {
		mouseButtons = new int[GLFW_MOUSE_BUTTON_LAST];
		lastMousePositionX = 0.0f;
		lastMousePositionY = 0.0f;
		mousePositionX = 0.0f;
		mousePositionY = 0.0f;
		mouseDeltaX = 0.0f;
		mouseDeltaY = 0.0f;
		mouseWheel = 0.0f;
		displaySelected = false;

		// Sets the mouse callbacks.
		glfwSetScrollCallback(FlounderDevices.getDisplay().getWindow(), callbackScroll = new GLFWScrollCallback() {
			@Override
			public void invoke(long window, double xoffset, double yoffset) {
				mouseWheel = (float) yoffset;
			}
		});

		glfwSetMouseButtonCallback(FlounderDevices.getDisplay().getWindow(), callbackMouseButton = new GLFWMouseButtonCallback() {
			@Override
			public void invoke(long window, int button, int action, int mods) {
				mouseButtons[button] = action;
			}
		});

		glfwSetCursorPosCallback(FlounderDevices.getDisplay().getWindow(), callbackCursorPos = new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double xpos, double ypos) {
				mousePositionX = (float) (xpos / FlounderDevices.getDisplay().getWidth());
				mousePositionY = (float) (ypos / FlounderDevices.getDisplay().getHeight());
			}
		});

		glfwSetCursorEnterCallback(FlounderDevices.getDisplay().getWindow(), callbackCursorEnter = new GLFWCursorEnterCallback() {
			@Override
			public void invoke(long window, boolean entered) {
				displaySelected = entered;
			}
		});
	}

	/**
	 * Updates the mouse system. Should be called once every frame.
	 *
	 * @param delta The time in seconds since the last frame.
	 */
	protected void update(float delta) {
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

		addProfileValues();
	}

	private void addProfileValues() {
		if (FlounderProfiler.isOpen()) {
			FlounderProfiler.add("Mouse", "Position X", mousePositionX);
			FlounderProfiler.add("Mouse", "Position Y", mousePositionY);
			FlounderProfiler.add("Mouse", "Delta X", mouseDeltaX);
			FlounderProfiler.add("Mouse", "Delta Y", mouseDeltaY);
			FlounderProfiler.add("Mouse", "Wheel", mouseWheel);
			FlounderProfiler.add("Mouse", "Display Selected", displaySelected);
		}
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

	/**
	 * Closes the GLFW mouse system, do not use the mouse after calling this.
	 */
	protected void dispose() {
		callbackScroll.free();
		callbackMouseButton.free();
		callbackCursorPos.free();
		callbackCursorEnter.free();
	}
}
