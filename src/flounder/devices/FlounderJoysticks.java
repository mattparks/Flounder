package flounder.devices;

import flounder.framework.*;
import flounder.logger.*;
import flounder.platform.*;

/**
 * A module used for the creation, updating and destruction of joysticks.
 */
public class FlounderJoysticks extends Module {
	/**
	 * Creates a new GLFW joystick manager.
	 */
	public FlounderJoysticks() {
		super(FlounderPlatform.class, FlounderLogger.class, FlounderDisplay.class);
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
	}

	@Handler.Function(Handler.FLAG_UPDATE_PRE)
	public void update() {
	}

	/**
	 * Determines if the GLFW joystick is connected
	 *
	 * @param joystick The joystick to check connection with.
	 *
	 * @return If the joystick is connected.
	 */
	@Module.MethodReplace
	public boolean isConnected(int joystick) {
		return false;
	}

	/**
	 * Gets the name of the joystick.
	 *
	 * @param joystick The joystick of interest.
	 *
	 * @return The joysticks name.
	 */
	@Module.MethodReplace
	public String getName(int joystick) {
		return "NULL";
	}

	/**
	 * Gets the value of a joystick's axis.
	 *
	 * @param joystick The joystick of interest.
	 * @param axis The axis of interest.
	 *
	 * @return The value of the joystick's axis.
	 */
	@Module.MethodReplace
	public float getAxis(int joystick, int axis) {
		return 0.0f;
	}

	/**
	 * Gets the whether a button on a joystick is pressed.
	 *
	 * @param joystick The joystick of interest.
	 * @param button The button of interest.
	 *
	 * @return Whether a button on a joystick is pressed.
	 */
	@Module.MethodReplace
	public boolean getButton(int joystick, int button) {
		return false;
	}

	/**
	 * Gets the number of axes the joystick offers.
	 *
	 * @param joystick The joystick of interest.
	 *
	 * @return The number of axes the joystick offers.
	 */
	public int getCountAxes(int joystick) {
		return 0;
	}

	/**
	 * Gets the number of buttons the joystick offers.
	 *
	 * @param joystick The joystick of interest.
	 *
	 * @return The number of buttons the joystick offers.
	 */
	public int getCountButtons(int joystick) {
		return 0;
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
	}

	@Module.Instance
	public static FlounderJoysticks get() {
		return (FlounderJoysticks) Framework.get().getInstance(FlounderJoysticks.class);
	}
}
