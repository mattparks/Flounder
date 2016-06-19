package flounder.inputs;

import flounder.engine.*;

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
		super((int code) -> FlounderEngine.getDevices().getMouse().getMouse(code), mouseButtons);
	}
}
