package flounder.inputs;

import flounder.devices.*;
import flounder.maths.*;

/**
 * Axis from a joystick.
 */
public class JoystickAxis implements IAxis {
	private final int joystick;
	private final int[] joystickAxes;

	/**
	 * Creates a new JoystickAxis.
	 *
	 * @param joystick The joystick. Should be one of the IInput.JOYSTICK values.
	 * @param joystickAxes The axes on the joystick being checked.
	 */
	public JoystickAxis(final int joystick, final int... joystickAxes) {
		this.joystick = joystick;
		this.joystickAxes = joystickAxes;
	}

	@Override
	public float getAmount() {
		if (joystickAxes == null || joystick == -1) {
			return 0.0f;
		}

		float result = 0.0f;

		for (int joystickAxe : joystickAxes) {
			result += (FlounderDevices.getJoysticks() != null && FlounderDevices.getJoysticks().isConnected(joystick) ? FlounderDevices.getJoysticks().getAxis(joystick, joystickAxe) : 0.0f);
		}

		return Maths.clamp(result, -1.0f, 1.0f);
	}
}
