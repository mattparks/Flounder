package flounder.guis;

import flounder.devices.*;
import flounder.inputs.*;

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

	public GuiSelector() {
		cursorX = 0.0f;
		cursorY = 0.0f;
		leftClick = false;
		rightClick = false;

		mouseLeft = new MouseButton(GLFW_MOUSE_BUTTON_LEFT);
		mouseRight = new MouseButton(GLFW_MOUSE_BUTTON_RIGHT);
	}

	protected void update() {
		cursorX = ManagerDevices.getMouse().getPositionX();
		cursorY = ManagerDevices.getMouse().getPositionY();
		leftClick = mouseLeft.isDown();
		rightClick = mouseRight.isDown();
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
