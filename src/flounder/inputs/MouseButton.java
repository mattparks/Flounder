package flounder.inputs;

import flounder.devices.*;

/**
 * Handles buttons on a mouse.
 */
public class MouseButton extends BaseButton {
	/**
	 * Creates a new MouseButton.
	 *
	 * @param mouseButtons The codes for the mouse buttons being checked. They should be GLFW.MOUSE_BUTTON values.
	 */
	public MouseButton(int... mouseButtons) {
		super((int code) -> FlounderMouse.get().getMouse(code), mouseButtons);
	}
}
