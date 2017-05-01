package flounder.devices;

import flounder.framework.*;
import flounder.logger.*;
import flounder.platform.*;

import java.nio.*;

import static org.lwjgl.glfw.GLFW.*;

/**
 * A module used for the creation, updating and destruction of joysticks.
 */
public class FlounderJoysticks extends Module {
	private FloatBuffer joystickAxes[];
	private ByteBuffer joystickButtons[];
	private String joystickNames[];

	/**
	 * Creates a new GLFW joystick manager.
	 */
	public FlounderJoysticks() {
		super(FlounderLogger.class, FlounderDisplay.class);
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
		this.joystickAxes = new FloatBuffer[GLFW_JOYSTICK_LAST];
		this.joystickButtons = new ByteBuffer[GLFW_JOYSTICK_LAST];
		this.joystickNames = new String[GLFW_JOYSTICK_LAST];
	}

	@Handler.Function(Handler.FLAG_UPDATE_PRE)
	public void update() {
		// For each joystick check if connected and update.
		for (int i = 0; i < GLFW_JOYSTICK_LAST; i++) {
			if (glfwJoystickPresent(i)) {
				if (joystickAxes[i] == null || joystickButtons[i] == null || joystickNames[i] == null) {
					FlounderLogger.get().log("Connecting Joystick: " + i);
					joystickAxes[i] = FlounderPlatform.get().createFloatBuffer(glfwGetJoystickAxes(i).capacity());
					joystickButtons[i] = FlounderPlatform.get().createByteBuffer(glfwGetJoystickButtons(i).capacity());
					joystickNames[i] = glfwGetJoystickName(i);
				}

				joystickAxes[i].clear();
				joystickAxes[i].put(glfwGetJoystickAxes(i));

				joystickButtons[i].clear();
				joystickButtons[i].put(glfwGetJoystickButtons(i));
			} else {
				if (joystickAxes[i] != null || joystickButtons[i] != null || joystickNames[i] != null) {
					FlounderLogger.get().log("Disconnecting Joystick: " + i);
					joystickAxes[i].clear();
					joystickAxes[i] = null;

					joystickButtons[i].clear();
					joystickButtons[i] = null;
					joystickNames[i] = null;
				}
			}
		}
	}

	@Handler.Function(Handler.FLAG_PROFILE)
	public void profile() {
	}

	/**
	 * Determines if the GLFW joystick is connected
	 *
	 * @param joystick The joystick to check connection with.
	 *
	 * @return If the joystick is connected.
	 */
	public boolean isConnected(int joystick) {
		return joystick >= 0 && joystick < GLFW_JOYSTICK_LAST && this.joystickNames[joystick] != null;
	}

	/**
	 * Gets the name of the joystick.
	 *
	 * @param joystick The joystick of interest.
	 *
	 * @return The joysticks name.
	 */
	public String getName(int joystick) {
		return this.joystickNames[joystick];
	}

	/**
	 * Gets the value of a joystick's axis.
	 *
	 * @param joystick The joystick of interest.
	 * @param axis The axis of interest.
	 *
	 * @return The value of the joystick's axis.
	 */
	public float getAxis(int joystick, int axis) {
		return this.joystickAxes[joystick].get(axis);
	}

	/**
	 * Gets the whether a button on a joystick is pressed.
	 *
	 * @param joystick The joystick of interest.
	 * @param button The button of interest.
	 *
	 * @return Whether a button on a joystick is pressed.
	 */
	public boolean getButton(int joystick, int button) {
		return this.joystickButtons[joystick].get(button) != 0;
	}

	/**
	 * Gets the number of axes the joystick offers.
	 *
	 * @param joystick The joystick of interest.
	 *
	 * @return The number of axes the joystick offers.
	 */
	public int getCountAxes(int joystick) {
		return this.joystickAxes[joystick].capacity();
	}

	/**
	 * Gets the number of buttons the joystick offers.
	 *
	 * @param joystick The joystick of interest.
	 *
	 * @return The number of buttons the joystick offers.
	 */
	public int getCountButtons(int joystick) {
		return this.joystickButtons[joystick].capacity();
	}


	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
	}

	@Module.Instance
	public static FlounderJoysticks get() {
		return (FlounderJoysticks) Framework.getInstance(FlounderJoysticks.class);
	}

	@Module.TabName
	public static String getTab() {
		return "Joysicks";
	}
}
