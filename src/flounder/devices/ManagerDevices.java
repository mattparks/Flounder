package flounder.devices;

import flounder.engine.*;
import org.lwjgl.glfw.*;

import java.awt.*;
import java.text.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * Manages the GLFW devices.
 */
public class ManagerDevices {
	private static boolean initialized;
	private static DeviceDisplay display;
	private static DeviceKeyboard keyboard;
	private static DeviceMouse mouse;
	private static DeviceJoysticks joysticks;
	private static DeviceSound sound;

	/**
	 * Creates GLFW devices.
	 *
	 * @param displayCanvas
	 * @param displayWidth The window width in pixels.
	 * @param displayHeight The window height in pixels.
	 * @param displayTitle The window title.
	 * @param displayVSync If the window will use vSync..
	 * @param displayAntialiasing If OpenGL will use altialiasing.
	 * @param displaySamples How many MFAA samples should be done before swapping display buffers. Zero disables multisampling. GLFW_DONT_CARE means no preference.
	 * @param displayFullscreen If the window will start fullscreen.
	 */
	public static void init(final Canvas displayCanvas, final int displayWidth, final int displayHeight, final String displayTitle, final boolean displayVSync, final boolean displayAntialiasing, final int displaySamples, final boolean displayFullscreen) {
		if (!initialized) {
			glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));
			display = new DeviceDisplay(displayCanvas, displayWidth, displayHeight, displayTitle, displayVSync, displayAntialiasing, displaySamples, displayFullscreen);
			keyboard = new DeviceKeyboard();
			mouse = new DeviceMouse();
			joysticks = new DeviceJoysticks();
			sound = new DeviceSound();

			// Logs OpenGL version info.
			FlounderLogger.log("Number of Cores: " + Runtime.getRuntime().availableProcessors());
			FlounderLogger.log("OpenGL Version: " + glGetString(GL_VERSION));
			initialized = true;
		}
	}

	/**
	 * Updates the before frame device systems.
	 *
	 * @param delta The time in seconds since the last frame.
	 */
	public static void preRender(final float delta) {
		display.pollEvents();
		joysticks.update(delta);
		keyboard.update(delta);
		mouse.update(delta);
		sound.update(delta);
	}

	/**
	 * Updates the after frame device systems.
	 */
	public static void postRender() {
		display.swapBuffers();
	}

	/**
	 * Gets the current display device manager.
	 *
	 * @return The current display device manager.
	 */
	public static DeviceDisplay getDisplay() {
		return display;
	}

	/**
	 * Gets the current keyboard device manager.
	 *
	 * @return The current keyboard device manager.
	 */
	public static DeviceKeyboard getKeyboard() {
		return keyboard;
	}

	/**
	 * Gets the current mouse device.
	 *
	 * @return The current mouse device.
	 */
	public static DeviceMouse getMouse() {
		return mouse;
	}

	/**
	 * Gets the current joystick device manager.
	 *
	 * @return The current joystick device manager.
	 */
	public static DeviceJoysticks getJoysticks() {
		return joysticks;
	}

	/**
	 * Gets the current sound device.
	 *
	 * @return The current sound device.
	 */
	public static DeviceSound getSound() {
		return sound;
	}

	/**
	 * @return Returns memory stats in a string.
	 */
	public static StringBuilder getUsedMemory() {
		final Runtime runtime = Runtime.getRuntime();
		final NumberFormat format = NumberFormat.getInstance();

		final StringBuilder sb = new StringBuilder();
		final long maxMemory = runtime.maxMemory();
		final long allocatedMemory = runtime.totalMemory();
		final long freeMemory = runtime.freeMemory();

		sb.append("Free memory: " + format.format(freeMemory / 1024) + "\n");
		sb.append("Allocated memory: " + format.format(allocatedMemory / 1024) + "\n");
		sb.append("Max memory: " + format.format(maxMemory / 1024) + "\n");
		sb.append("Total free memory: " + format.format((freeMemory + maxMemory - allocatedMemory) / 1024) + "\n");
		return sb;
	}

	/**
	 * Closes the GLFW devices, do not use device objects after calling this.
	 */
	public static void dispose() {
		if (initialized) {
			joysticks.dispose();
			keyboard.dispose();
			mouse.dispose();
			sound.dispose();
			display.dispose();
			initialized = false;
		}
	}
}
