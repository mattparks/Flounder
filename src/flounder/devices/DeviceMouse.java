package flounder.devices;

import org.lwjgl.glfw.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * Manages the creation, updating and destruction of the mouse.
 */
public class DeviceMouse {
	private final GLFWScrollCallback callbackScroll;
	private final GLFWMouseButtonCallback callbackMouseButton;
	private final GLFWCursorPosCallback callbackCursorPos;
	private final GLFWCursorEnterCallback callbackCursorEnter;

	private int mouseButtons[];
	private float lastMousePositionX;
	private float lastMousePositionY;
	private float mousePositionX;
	private float mousePositionY;
	private float mouseDeltaX;
	private float mouseDeltaY;
	private float mouseDeltaWheel;
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
		mouseDeltaWheel = 0.0f;
		displaySelected = false;

		// Sets the mouse callbacks.
		glfwSetScrollCallback(ManagerDevices.getDisplay().getWindow(), callbackScroll = new GLFWScrollCallback() {
			@Override
			public void invoke(long window, double xoffset, double yoffset) {
				mouseDeltaWheel += yoffset;
			}
		});

		glfwSetMouseButtonCallback(ManagerDevices.getDisplay().getWindow(), callbackMouseButton = new GLFWMouseButtonCallback() {
			@Override
			public void invoke(long window, int button, int action, int mods) {
				// Actions: GLFW_PRESS   GLFW_RELEASE   GLFW_REPEAT
				mouseButtons[button] = action;
			}
		});

		glfwSetCursorPosCallback(ManagerDevices.getDisplay().getWindow(), callbackCursorPos = new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double xpos, double ypos) {
				mousePositionX = (float) (xpos / ManagerDevices.getDisplay().getDisplayWidth());
				mousePositionY = (float) (ypos / ManagerDevices.getDisplay().getDisplayHeight());
			}
		});

		glfwSetCursorEnterCallback(ManagerDevices.getDisplay().getWindow(), callbackCursorEnter = new GLFWCursorEnterCallback() {
			@Override
			public void invoke(long window, int entered) {
				displaySelected = entered == GL_TRUE;
			}
		});
	}

	/**
	 * Updates the mouse system. Should be called once every frame.
	 *
	 * @param delta The time in seconds since the last frame.
	 */
	protected void update(final float delta) {
		mouseDeltaX = (lastMousePositionX - mousePositionX) * delta;
		mouseDeltaY = (lastMousePositionY - mousePositionY) * delta;

		lastMousePositionX = mousePositionX;
		lastMousePositionY = mousePositionY;

		mouseDeltaWheel += ((mouseDeltaWheel > 0) ? -25 : 25) * delta;
		mouseDeltaWheel = Math.abs(mouseDeltaWheel) >= Math.abs(0.875f) ? mouseDeltaWheel : 0;
	}

	/**
	 * Gets whether or not a particular mouse button is currently pressed.
	 *
	 * @param button The mouse button to test.
	 *
	 * @return If the mouse button is currently pressed.
	 */
	public boolean getMouse(final int button) {
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
		return mouseDeltaWheel;
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
		callbackScroll.release();
		callbackMouseButton.release();
		callbackCursorPos.release();
		callbackCursorEnter.release();
	}
}
