package flounder.inputs;

import flounder.devices.*;

/**
 * Handles buttons on a mouse.
 */
public class MouseButton extends BaseButton {
	/**
	 * Creates a new MouseButton.
	 *
	 * @param mouseButtons The codes for the mouse buttons being checked. They should be IInput.MOUSE_BUTTON values.
	 */
	public MouseButton(int... mouseButtons) {
		super((int code) -> FlounderDevices.getMouse() != null && FlounderDevices.getMouse().getMouse(code), mouseButtons);
	}
}
