package flounder.inputs;

import flounder.devices.*;

/**
 * Handles buttons from a keyboard.
 */
public class KeyButton extends BaseButton {
	/**
	 * Creates a new KeyButton.
	 *
	 * @param keyCodes The codes for the buttons being checked. They should be IInput.KEY values.
	 */
	public KeyButton(int... keyCodes) {
		super((int code) -> FlounderDevices.getKeyboard() != null && FlounderDevices.getKeyboard().getKey(code), keyCodes);
	}
}
