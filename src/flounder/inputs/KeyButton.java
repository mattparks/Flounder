package flounder.inputs;

import flounder.devices.*;

/**
 * Handles buttons from a keyboard.
 */
public class KeyButton extends BaseButton {
	/**
	 * Creates a new KeyButton.
	 *
	 * @param keyCodes The codes for the buttons being checked. They should be GLFW.KEY values.
	 */
	public KeyButton(int... keyCodes) {
		super((int code) -> FlounderKeyboard.getKey(code), keyCodes);
	}
}
