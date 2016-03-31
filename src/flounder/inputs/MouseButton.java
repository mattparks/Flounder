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
		super((int code) -> (ManagerDevices.getMouse() != null ? ManagerDevices.getMouse().getMouse(code) : false), mouseButtons);
	}
}
