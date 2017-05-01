package flounder.guis;

import flounder.devices.*;
import flounder.framework.*;
import flounder.inputs.*;
import flounder.maths.*;
import flounder.profiling.*;

import static flounder.platform.Constants.*;

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
		leftClick = mouseLeft.isDown();
		rightClick = mouseRight.isDown();
		leftWasClick = mouseLeft.wasDown();
		rightWasClick = mouseRight.wasDown();

		cursorX = FlounderMouse.get().getPositionX();
		cursorY = FlounderMouse.get().getPositionY();

		if (joysticksInitialized && FlounderJoysticks.get().isConnected(selectedJoystick) && FlounderGuis.get().getGuiMaster().isGamePaused()) {
			if (Math.abs(Maths.deadband(0.1f, joystickAxisX.getAmount())) > 0.0 || Math.abs(Maths.deadband(0.1f, joystickAxisY.getAmount())) > 0.0) {
				cursorX += (joystickAxisX.getAmount()) * 0.75f * Framework.getDelta();
				cursorY += (-joystickAxisY.getAmount()) * 0.75f * Framework.getDelta();
				cursorX = Maths.clamp(cursorX, 0.0f, 1.0f);
				cursorY = Maths.clamp(cursorY, 0.0f, 1.0f);
				FlounderMouse.get().setPosition(cursorX * FlounderDisplay.get().getWidth(), cursorY * FlounderDisplay.get().getHeight());
			}

			leftClick = leftClick || joystickLeft.isDown();
			rightClick = rightClick || joystickRight.isDown();
			leftWasClick = leftWasClick || joystickLeft.wasDown();
			rightWasClick = rightWasClick || joystickRight.wasDown();
		}

		if (FlounderProfiler.get().isOpen()) {
			FlounderProfiler.get().add(FlounderGuis.getTab(), "Selector X", cursorX);
			FlounderProfiler.get().add(FlounderGuis.getTab(), "Selector Y", cursorY);
			FlounderProfiler.get().add(FlounderGuis.getTab(), "Click Left", leftClick);
			FlounderProfiler.get().add(FlounderGuis.getTab(), "Click Right", rightClick);
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

		// TODO: Account for rotations.
		float positionX = object.getPosition().x;
		float positionY = object.getPosition().y;

		float width = 2.0f * object.getMeshSize().x * object.getScreenDimensions().x / FlounderDisplay.get().getAspectRatio();
		float height = 2.0f * object.getMeshSize().y * object.getScreenDimensions().y;

		if (FlounderMouse.get().isDisplaySelected() && FlounderDisplay.get().isFocused()) {
			if (FlounderGuis.get().getSelector().getCursorX() >= positionX - (width / 2.0f) && FlounderGuis.get().getSelector().getCursorX() <= positionX + (width / 2.0f)) {
				if (FlounderGuis.get().getSelector().getCursorY() >= positionY - (height / 2.0f) && FlounderGuis.get().getSelector().getCursorY() <= positionY + (height / 2.0f)) {
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
