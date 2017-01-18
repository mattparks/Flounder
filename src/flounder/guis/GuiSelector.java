package flounder.guis;

import flounder.devices.*;
import flounder.framework.*;
import flounder.inputs.*;
import flounder.maths.*;
import flounder.profiling.*;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Represents a virtual cursor that will be used to determine if a GUI action was preformed by a device.
 */
public class GuiSelector {
	private float cursorX, cursorY;
	private boolean leftClick, rightClick;
	private boolean leftWasClick, rightWasClick;

	private MouseButton mouseLeft;
	private MouseButton mouseRight;
	private int selectedJoystick;
	private JoystickAxis joystickAxisX;
	private JoystickAxis joystickAxisY;
	private JoystickButton joystickLeft;
	private JoystickButton joystickRight;

	public GuiSelector() {
		cursorX = 0.0f;
		cursorY = 0.0f;
		leftClick = false;
		rightClick = false;

		mouseLeft = new MouseButton(GLFW_MOUSE_BUTTON_LEFT);
		mouseRight = new MouseButton(GLFW_MOUSE_BUTTON_RIGHT);
	}

	public void initJoysticks(int joystick, int joystickLeftClick, int joystickRightClick, int joystickAxisX, int joystickAxisY) {
		this.selectedJoystick = joystick;
		this.joystickAxisX = new JoystickAxis(joystick, joystickAxisX);
		this.joystickAxisY = new JoystickAxis(joystick, joystickAxisY);
		this.joystickLeft = new JoystickButton(joystick, joystickLeftClick);
		this.joystickRight = new JoystickButton(joystick, joystickRightClick);
	}

	protected void update() {
		if (FlounderJoysticks.isConnected(selectedJoystick)) {
			if (Math.abs(Maths.deadband(0.1f, joystickAxisX.getAmount())) > 0.0 || Math.abs(Maths.deadband(0.1f, joystickAxisY.getAmount())) > 0.0) {
				cursorX += (joystickAxisX.getAmount()) * 0.75f * FlounderFramework.getDelta();
				cursorY += (-joystickAxisY.getAmount()) * 0.75f * FlounderFramework.getDelta();
				cursorX = Maths.clamp(cursorX, 0.0f, 1.0f);
				cursorY = Maths.clamp(cursorY, 0.0f, 1.0f);
			}

			leftClick = joystickLeft.isDown();
			rightClick = joystickRight.isDown();
			leftWasClick = joystickLeft.wasDown();
			rightWasClick = joystickRight.wasDown();
		} else {
			cursorX = FlounderMouse.getPositionX();
			cursorY = FlounderMouse.getPositionY();

			leftClick = mouseLeft.isDown();
			rightClick = mouseRight.isDown();
			leftWasClick = mouseLeft.wasDown();
			rightWasClick = mouseRight.wasDown();
		}

		if (FlounderProfiler.isOpen()) {
			FlounderProfiler.add(FlounderGuis.PROFILE_TAB_NAME, "Selector X", cursorX);
			FlounderProfiler.add(FlounderGuis.PROFILE_TAB_NAME, "Selector Y", cursorY);
			FlounderProfiler.add(FlounderGuis.PROFILE_TAB_NAME, "Click Left", leftClick);
			FlounderProfiler.add(FlounderGuis.PROFILE_TAB_NAME, "Click Right", rightClick);
		}
	}

	public float getCursorX() {
		return cursorX;
	}

	public float getCursorY() {
		return cursorY;
	}

	public boolean isLeftClick() {
		return leftClick;
	}

	public boolean isRightClick() {
		return rightClick;
	}

	public void cancelWasEvent() {
		leftWasClick = false;
		rightWasClick = false;
	}

	public boolean wasLeftClick() {
		return leftWasClick;
	}

	public boolean wasRightClick() {
		return rightWasClick;
	}
}
