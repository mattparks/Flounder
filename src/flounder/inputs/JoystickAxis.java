package flounder.inputs;

import flounder.engine.*;
import flounder.maths.*;

/**
 * Axis from a joystick.
 */
public class JoystickAxis implements IAxis {
	private int joystick;
	private int[] joystickAxes;

	/**
	 * Creates a new JoystickAxis.
	 *
	 * @param joystick The joystick. Should be one of the GLFW.JOYSTICK values.
	 * @param joystickAxes The axes on the joystick being checked.
	 */
	public JoystickAxis(int joystick, int... joystickAxes) {
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
			result += (FlounderEngine.getDevices().getJoysticks().isConnected(joystick) ? FlounderEngine.getDevices().getJoysticks().getAxis(joystick, joystickAxe) : 0.0f);
		}

		return Maths.clamp(result, -1.0f, 1.0f);
	}
}
