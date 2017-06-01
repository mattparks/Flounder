package flounder.lwjgl3.devices;

import flounder.devices.*;
import flounder.framework.*;
import flounder.logger.*;
import flounder.platform.*;

import java.nio.*;

import static org.lwjgl.glfw.GLFW.*;

@Module.ModuleOverride
public class LwjglJoysicks extends FlounderJoysticks {
	private FloatBuffer joystickAxes[];
	private ByteBuffer joystickButtons[];
	private String joystickNames[];

	public LwjglJoysicks() {
		super();
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
		this.joystickAxes = new FloatBuffer[GLFW_JOYSTICK_LAST];
		this.joystickButtons = new ByteBuffer[GLFW_JOYSTICK_LAST];
		this.joystickNames = new String[GLFW_JOYSTICK_LAST];

		super.init();
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

		super.update();
	}

	@Override
	public boolean isConnected(int joystick) {
		return joystick >= 0 && joystick < GLFW_JOYSTICK_LAST && this.joystickNames[joystick] != null;
	}

	@Override
	public String getName(int joystick) {
		return this.joystickNames[joystick];
	}

	@Override
	public float getAxis(int joystick, int axis) {
		return this.joystickAxes[joystick].get(axis);
	}

	@Override
	public boolean getButton(int joystick, int button) {
		return this.joystickButtons[joystick].get(button) != 0;
	}

	@Override
	public int getCountAxes(int joystick) {
		return this.joystickAxes[joystick].capacity();
	}

	@Override
	public int getCountButtons(int joystick) {
		return this.joystickButtons[joystick].capacity();
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
		super.dispose();
	}
}
