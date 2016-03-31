package flounder.inputs;

import flounder.devices.*;
import flounder.maths.*;

/**
 * Axis from a joystick.
 */
public class JoystickAxis implements IAxis {
	private final int m_joystick;
	private final int[] m_joystickAxes;

	/**
	 * Creates a new JoystickAxis.
	 *
	 * @param joystick The joystick. Should be one of the IInput.JOYSTICK values.
	 * @param joystickAxes The axes on the joystick being checked.
	 */
	public JoystickAxis(final int joystick, final int... joystickAxes) {
		m_joystick = joystick;
		m_joystickAxes = joystickAxes;
	}

	@Override
	public float getAmount() {
		if (m_joystickAxes == null || m_joystick == -1) {
			return 0.0f;
		}

		float result = 0.0f;

		for (int joystickAxe : m_joystickAxes) {
			result += (ManagerDevices.getJoysticks() != null && ManagerDevices.getJoysticks().isConnected(m_joystick) ? ManagerDevices.getJoysticks().getJoystick(m_joystick).getAxis(joystickAxe) : 0.0f);
		}

		return Maths.clamp(result, -1.0f, 1.0f);
	}
}
