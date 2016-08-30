package flounder.devices;

import flounder.engine.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * Manages the all GLFW devices in a separate thread.
 */
public class FlounderDevices implements IModule {
	private DeviceDisplay display;
	private DeviceJoysticks joysticks;
	private DeviceKeyboard keyboard;
	private DeviceMouse mouse;
	private DeviceSound sound;

	/**
	 * Creates all of the GLFW devices.
	 *
	 * @param width The window width in pixels.
	 * @param height The window height in pixels.
	 * @param title The window title.
	 * @param vsync If the window will use vSync..
	 * @param antialiasing If OpenGL will use antialiasing.
	 * @param samples How many MFAA samples should be done before swapping buffers. Zero disables multisampling. GLFW_DONT_CARE means no preference.
	 * @param fullscreen If the window will start fullscreen.
	 */
	public FlounderDevices(int width, int height, String title, boolean vsync, boolean antialiasing, int samples, boolean fullscreen) {
		display = new DeviceDisplay(width, height, title, vsync, antialiasing, samples, fullscreen);
		joysticks = new DeviceJoysticks();
		keyboard = new DeviceKeyboard();
		mouse = new DeviceMouse();
		sound = new DeviceSound();
	}

	@Override
	public void init() {
		display.init();
		joysticks.init();
		keyboard.init();
		mouse.init();
		sound.init();


		FlounderEngine.getLogger().log("===== This is not an error message, it is a system log. =====");
		FlounderEngine.getLogger().log("Flounder Engine Version: " + FlounderEngine.getVersion().version);

		FlounderEngine.getLogger().log("Flounder OpenGL Version: " + glGetString(GL_VERSION));    /* Total number of processors or cores available to the JVM */

		FlounderEngine.getLogger().log("Flounder Available Processors (cores): " + Runtime.getRuntime().availableProcessors());

		FlounderEngine.getLogger().log("Flounder Free Memory (bytes): " + Runtime.getRuntime().freeMemory());

		long maxMemory = Runtime.getRuntime().maxMemory();
		FlounderEngine.getLogger().log("Flounder Maximum Memory (bytes): " + (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory));

		FlounderEngine.getLogger().log("Flounder Total Memory Available To JVM (bytes): " + Runtime.getRuntime().totalMemory());
		FlounderEngine.getLogger().log("===== End of system log. =====\n");
	}

	@Override
	public void update() {
		display.update();
		joysticks.update();
		keyboard.update();
		mouse.update();
		sound.update();
	}

	/**
	 * Updates the after frame device systems.
	 */
	public void swapBuffers() {
		display.swapBuffers();
	}

	@Override
	public void profile() {
		display.profile();
		joysticks.profile();
		keyboard.profile();
		mouse.profile();
		sound.profile();
	}

	/**
	 * Gets the current display device manager.
	 *
	 * @return The current display device manager.
	 */
	public DeviceDisplay getDisplay() {
		return display;
	}

	/**
	 * Gets the current joystick device manager.
	 *
	 * @return The current joystick device manager.
	 */
	public DeviceJoysticks getJoysticks() {
		return joysticks;
	}

	/**
	 * Gets the current keyboard device manager.
	 *
	 * @return The current keyboard device manager.
	 */
	public DeviceKeyboard getKeyboard() {
		return keyboard;
	}

	/**
	 * Gets the current mouse device.
	 *
	 * @return The current mouse device.
	 */
	public DeviceMouse getMouse() {
		return mouse;
	}

	/**
	 * Gets the current sound device.
	 *
	 * @return The current sound device.
	 */
	public DeviceSound getSound() {
		return sound;
	}

	/**
	 * @return The current GLFW time time in seconds.
	 */
	public float getTime() {
		return (float) (glfwGetTime() * 1000.0f);
	}

	@Override
	public void dispose() {
		sound.dispose();
		mouse.dispose();
		keyboard.dispose();
		joysticks.dispose();
		display.dispose();
	}
}
