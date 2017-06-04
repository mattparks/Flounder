package flounder.devices;

import flounder.framework.*;
import flounder.logger.*;
import flounder.platform.*;

/**
 * A module used for the creation, updating and destruction of the mouse.
 */
public class FlounderMouse extends Module {
	/**
	 * Creates a new GLFW mouse manager.
	 */
	public FlounderMouse() {
		super(FlounderPlatform.class, FlounderLogger.class, FlounderDisplay.class);
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
	}

	@Handler.Function(Handler.FLAG_UPDATE_PRE)
	public void update() {
	}

	/**
	 * Sets if the operating systems cursor is hidden whilst in the display.
	 *
	 * @param disabled If the system cursor should be disabled or hidden when not shown.
	 */
	@Module.MethodReplace
	public void setCursorHidden(boolean disabled) {
	}

	/**
	 * Gets whether or not a particular mouse button is currently pressed.
	 * <p>GLFW Actions: GLFW_PRESS, GLFW_RELEASE, GLFW_REPEAT</p>
	 *
	 * @param button The mouse button to test.
	 *
	 * @return If the mouse button is currently pressed.
	 */
	@Module.MethodReplace
	public boolean getMouse(int button) {
		return false;
	}

	/**
	 * Gets the mouses screen x position.
	 *
	 * @return The mouses x position.
	 */
	@Module.MethodReplace
	public float getPositionX() {
		return 0.0f;
	}

	/**
	 * Gets the mouses screen y position.
	 *
	 * @return The mouses y position.
	 */
	@Module.MethodReplace
	public float getPositionY() {
		return 0.0f;
	}

	@Module.MethodReplace
	public void setPosition(float cursorX, float cursorY) {
	}

	/**
	 * Gets the mouses delta x.
	 *
	 * @return The mouses delta x.
	 */
	@Module.MethodReplace
	public float getDeltaX() {
		return 0.0f;
	}

	/**
	 * Gets the mouses delta y.
	 *
	 * @return The mouses delta y.
	 */
	@Module.MethodReplace
	public float getDeltaY() {
		return 0.0f;
	}

	/**
	 * Gets the mouses wheel delta.
	 *
	 * @return The mouses wheel delta.
	 */
	@Module.MethodReplace
	public float getDeltaWheel() {
		return 0.0f;
	}

	/**
	 * Gets if the display is selected.
	 *
	 * @return If the display is selected.
	 */
	@Module.MethodReplace
	public boolean isDisplaySelected() {
		return true;
	}

	/**
	 * If the cursor is hidden, the mouse is the display locked if true.
	 *
	 * @return If the cursor is hidden.
	 */
	@Module.MethodReplace
	public boolean isCursorDisabled() {
		return false;
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
	}

	@Module.Instance
	public static FlounderMouse get() {
		return (FlounderMouse) Framework.get().getInstance(FlounderMouse.class);
	}

	@Module.TabName
	public static String getTab() {
		return "Mouse";
	}
}
