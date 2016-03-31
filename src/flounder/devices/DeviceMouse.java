package flounder.devices;

import org.lwjgl.glfw.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * Manages the creation, updating and destruction of the mouse.
 */
public class DeviceMouse {
	private final GLFWScrollCallback m_callbackScroll;
	private final GLFWMouseButtonCallback m_callbackMouseButton;
	private final GLFWCursorPosCallback m_callbackCursorPos;
	private final GLFWCursorEnterCallback m_callbackCursorEnter;

	private int m_buttons[];
	private float m_lastMousePositionX;
	private float m_lastMousePositionY;
	private float m_mousePositionX;
	private float m_mousePositionY;
	private float m_mouseDeltaX;
	private float m_mouseDeltaY;
	private float m_mouseDeltaWheel;
	private boolean m_displaySelected;

	/**
	 * Creates a new GLFW mouse.
	 */
	protected DeviceMouse() {
		m_buttons = new int[GLFW_MOUSE_BUTTON_LAST];
		m_lastMousePositionX = 0.0f;
		m_lastMousePositionY = 0.0f;
		m_mousePositionX = 0.0f;
		m_mousePositionY = 0.0f;
		m_mouseDeltaX = 0.0f;
		m_mouseDeltaY = 0.0f;
		m_mouseDeltaWheel = 0.0f;
		m_displaySelected = false;

		// Sets the mouse callbacks.
		glfwSetScrollCallback(ManagerDevices.getDisplay().getWindow(), m_callbackScroll = new GLFWScrollCallback() {
			@Override
			public void invoke(long window, double xoffset, double yoffset) {
				m_mouseDeltaWheel += yoffset;
			}
		});

		glfwSetMouseButtonCallback(ManagerDevices.getDisplay().getWindow(), m_callbackMouseButton = new GLFWMouseButtonCallback() {
			@Override
			public void invoke(long window, int button, int action, int mods) {
				// Actions: GLFW_PRESS   GLFW_RELEASE   GLFW_REPEAT
				m_buttons[button] = action;
			}
		});

		glfwSetCursorPosCallback(ManagerDevices.getDisplay().getWindow(), m_callbackCursorPos = new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double xpos, double ypos) {
				m_mousePositionX = (float) (xpos / ManagerDevices.getDisplay().getDisplayWidth());
				m_mousePositionY = (float) (ypos / ManagerDevices.getDisplay().getDisplayHeight());
			}
		});

		glfwSetCursorEnterCallback(ManagerDevices.getDisplay().getWindow(), m_callbackCursorEnter = new GLFWCursorEnterCallback() {
			@Override
			public void invoke(long window, int entered) {
				m_displaySelected = entered == GL_TRUE;
			}
		});
	}

	/**
	 * Updates the mouse system. Should be called once every frame.
	 *
	 * @param delta The time in seconds since the last frame.
	 */
	protected void update(final float delta) {
		m_mouseDeltaX = (m_lastMousePositionX - m_mousePositionX) * delta;
		m_mouseDeltaY = (m_lastMousePositionY - m_mousePositionY) * delta;

		m_lastMousePositionX = m_mousePositionX;
		m_lastMousePositionY = m_mousePositionY;

		m_mouseDeltaWheel += ((m_mouseDeltaWheel > 0) ? -25 : 25) * delta;
		m_mouseDeltaWheel = Math.abs(m_mouseDeltaWheel) >= Math.abs(0.875f) ? m_mouseDeltaWheel : 0;
	}

	/**
	 * Gets whether or not a particular mouse button is currently pressed.
	 *
	 * @param button The mouse button to test.
	 *
	 * @return If the mouse button is currently pressed.
	 */
	public boolean getMouse(final int button) {
		return m_buttons[button] != GLFW_RELEASE;
	}

	/**
	 * Gets the mouses screen x position.
	 *
	 * @return The mouses x position.
	 */
	public float getPositionX() {
		return m_mousePositionX;
	}

	/**
	 * Gets the mouses screen y position.
	 *
	 * @return The mouses y position.
	 */
	public float getPositionY() {
		return m_mousePositionY;
	}

	/**
	 * Gets the mouses delta x.
	 *
	 * @return The mouses delta x.
	 */
	public float getDeltaX() {
		return m_mouseDeltaX;
	}

	/**
	 * Gets the mouses delta y.
	 *
	 * @return The mouses delta y.
	 */
	public float getDeltaY() {
		return m_mouseDeltaY;
	}

	/**
	 * Gets the mouses wheel delta.
	 *
	 * @return The mouses wheel delta.
	 */
	public float getDeltaWheel() {
		return m_mouseDeltaWheel;
	}

	/**
	 * Gets if the display is selected.
	 *
	 * @return If the display is selected.
	 */
	public boolean isDisplaySelected() {
		return m_displaySelected;
	}

	/**
	 * Closes the GLFW mouse system, do not use the mouse after calling this.
	 */
	protected void dispose() {
		m_callbackScroll.release();
		m_callbackMouseButton.release();
		m_callbackCursorPos.release();
		m_callbackCursorEnter.release();
	}
}
