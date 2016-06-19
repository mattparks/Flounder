package flounder.inputs;


import flounder.engine.*;

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
		super((int code) -> FlounderEngine.getDevices().getJoysticks().isConnected(joystick) && FlounderEngine.getDevices().getJoysticks().getButton(joystick, code), joystickButtons);
	}
}
