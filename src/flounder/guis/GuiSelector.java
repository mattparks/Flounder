package flounder.guis;

import flounder.devices.*;
import flounder.engine.*;
import flounder.engine.profiling.*;
import flounder.inputs.*;
import flounder.maths.*;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;

/**
 * Represents a virtual cursor that will be used to determine if a GUI action was preformed by a device.
 */
public class GuiSelector {
	// TODO: Joysticks.

	private float cursorX, cursorY;
	private boolean leftClick, rightClick;

	private MouseButton mouseLeft;
	private MouseButton mouseRight;
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
		joystickAxisX = new JoystickAxis(0, 0);
		joystickAxisY = new JoystickAxis(0, 1);
		joystickLeft = new JoystickButton(0, 0);
		joystickRight = new JoystickButton(0, 1);
	}

	protected void update() {
		if (ManagerDevices.getJoysticks().isConnected(0)) {
			if (Math.abs(Maths.deadband(0.1f, joystickAxisX.getAmount())) > 0.0 || Math.abs(Maths.deadband(0.1f, joystickAxisY.getAmount())) > 0.0) {
				cursorX += joystickAxisX.getAmount() * 0.3f * FlounderEngine.getDelta();
				cursorY += joystickAxisY.getAmount() * 0.3f * FlounderEngine.getDelta();
				cursorX = Maths.clamp(cursorX, 0.0f, 1.0f);
				cursorY = Maths.clamp(cursorY, 0.0f, 1.0f);

				leftClick = joystickLeft.isDown();
				rightClick = joystickRight.isDown();
			}
		} else {
			cursorX = ManagerDevices.getMouse().getPositionX();
			cursorY = ManagerDevices.getMouse().getPositionY();

			leftClick = mouseLeft.isDown();
			rightClick = mouseRight.isDown();
		}

		FlounderProfiler.add("Gui", "SelectorX", cursorX);
		FlounderProfiler.add("Gui", "SelectorY", cursorY);
		FlounderProfiler.add("Gui", "Click Left", leftClick);
		FlounderProfiler.add("Gui", "Click Right", rightClick);

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
}
