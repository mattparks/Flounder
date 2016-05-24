package flounder.guis;

import flounder.devices.*;
import flounder.engine.*;
import flounder.engine.profiling.*;
import flounder.inputs.*;
import flounder.maths.*;

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

	public void initJoysticks(final int joystick, final int joystickLeftClick, final int joystickRightClick, final int joystickAxisX, final int joystickAxisY) {
		this.selectedJoystick = joystick;
		this.joystickAxisX = new JoystickAxis(joystick, joystickAxisX);
		this.joystickAxisY = new JoystickAxis(joystick, joystickAxisY);
		this.joystickLeft = new JoystickButton(joystick, joystickLeftClick);
		this.joystickRight = new JoystickButton(joystick, joystickRightClick);
	}

	protected void update() {
		if (FlounderDevices.getJoysticks().isConnected(selectedJoystick)) {
			if (joystickAxisX != null && joystickAxisY != null) {
				if (Math.abs(Maths.deadband(0.1f, joystickAxisX.getAmount())) > 0.0 || Math.abs(Maths.deadband(0.1f, joystickAxisY.getAmount())) > 0.0) {
					cursorX += (joystickAxisX.getAmount()) * 0.5f * FlounderEngine.getDelta();
					cursorY += (joystickAxisY.getAmount()) * 0.5f * FlounderEngine.getDelta();
					cursorX = Maths.clamp(cursorX, 0.0f, 1.0f);
					cursorY = Maths.clamp(cursorY, 0.0f, 1.0f);
				}
			}

			if (joystickLeft != null && joystickRight != null) {
				leftClick = joystickLeft.isDown();
				rightClick = joystickRight.isDown();
				leftWasClick = joystickLeft.wasDown();
				rightWasClick = joystickRight.wasDown();
			}
		} else {
			cursorX = FlounderDevices.getMouse().getPositionX();
			cursorY = FlounderDevices.getMouse().getPositionY();

			leftClick = mouseLeft.isDown();
			rightClick = mouseRight.isDown();
			leftWasClick = mouseLeft.wasDown();
			rightWasClick = mouseRight.wasDown();
		}

		if (FlounderProfiler.isOpen()) {
			FlounderProfiler.add("GUI", "SelectorX", cursorX);
			FlounderProfiler.add("GUI", "SelectorY", cursorY);
			FlounderProfiler.add("GUI", "Click Left", leftClick);
			FlounderProfiler.add("GUI", "Click Right", rightClick);
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
