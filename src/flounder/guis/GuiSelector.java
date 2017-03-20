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
	private boolean joysticksInitialized;

	protected GuiSelector() {
		this.cursorX = 0.0f;
		this.cursorY = 0.0f;
		this.leftClick = false;
		this.rightClick = false;

		this.mouseLeft = new MouseButton(GLFW_MOUSE_BUTTON_LEFT);
		this.mouseRight = new MouseButton(GLFW_MOUSE_BUTTON_RIGHT);

		this.joysticksInitialized = false;
	}

	/**
	 * Sets up the joystick settings to be used for controlling the virtual cursor.
	 *
	 * @param joystick The joystick ID to attach though.
	 * @param joystickLeftClick The joystick key to be used as the left click.
	 * @param joystickRightClick The joystick key to be used as the right click.
	 * @param joystickAxisX The joystick axis to be used for moving the x axis.
	 * @param joystickAxisY The joystick axis to be used for moving the y axis.
	 */
	public void initJoysticks(int joystick, int joystickLeftClick, int joystickRightClick, int joystickAxisX, int joystickAxisY) {
		this.selectedJoystick = joystick;
		this.joystickAxisX = new JoystickAxis(joystick, joystickAxisX);
		this.joystickAxisY = new JoystickAxis(joystick, joystickAxisY);
		this.joystickLeft = new JoystickButton(joystick, joystickLeftClick);
		this.joystickRight = new JoystickButton(joystick, joystickRightClick);
		this.joysticksInitialized = true;
	}

	protected void update() {
		if (joysticksInitialized && FlounderJoysticks.isConnected(selectedJoystick)) {
			if (Math.abs(Maths.deadband(0.1f, joystickAxisX.getAmount())) > 0.0 || Math.abs(Maths.deadband(0.1f, joystickAxisY.getAmount())) > 0.0) {
				cursorX += (joystickAxisX.getAmount()) * 0.75f * Framework.getDelta();
				cursorY += (-joystickAxisY.getAmount()) * 0.75f * Framework.getDelta();
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

	/**
	 * Gets if the object provided has the cursor hovered above it.
	 *
	 * @param object The object to check with.
	 *
	 * @return If the object has the cursor inside of its box.
	 */
	public boolean isSelected(ScreenObject object) {
		if (object == null) {
			return false;
		}

		float positionX = object.getScreenPosition().x;
		float positionY = object.getScreenPosition().y;
		float dimensionsX = object.getScreenDimensions().x;
		float dimensionsY = object.getScreenDimensions().y;

		if (FlounderMouse.isDisplaySelected() && FlounderDisplay.isFocused()) {
			if (FlounderGuis.getSelector().getCursorX() >= positionX - (dimensionsX / 2.0f) && FlounderGuis.getSelector().getCursorX() <= positionX + (dimensionsX / 2.0f)) {
				if (FlounderGuis.getSelector().getCursorY() >= positionY - (dimensionsY / 2.0f) && FlounderGuis.getSelector().getCursorY() <= positionY + (dimensionsY / 2.0f)) {
					return true;
				}
			}
		}

		return false;
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
