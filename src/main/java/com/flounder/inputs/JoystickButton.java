package com.flounder.inputs;


import com.flounder.devices.*;

/**
 * Handles buttons on a joystick.
 */
public class JoystickButton extends BaseButton {
	/**
	 * Creates a new JoystickButton.
	 *
	 * @param joystick The joystick. Should be one of the GLFW.JOYSTICK values.
	 * @param joystickButtons The buttons on the joystick being checked.
	 */
	public JoystickButton(int joystick, int... joystickButtons) {
		super((int code) -> FlounderJoysticks.get().isConnected(joystick) && FlounderJoysticks.get().getButton(joystick, code), joystickButtons);
	}
}
