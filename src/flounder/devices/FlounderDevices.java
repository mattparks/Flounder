package flounder.devices;

import flounder.engine.*;
import org.lwjgl.glfw.*;

import java.text.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * Manages the GLFW devices.
 */
public class FlounderDevices implements Runnable {
	private static boolean initialized;
	private static DeviceDisplay display;
	private static DeviceKeyboard keyboard;
	private static DeviceMouse mouse;
	private static DeviceJoysticks joysticks;
	private static DeviceSound sound;

	private static float currentFrameTime;
	private static float lastFrameTime;
	private static float delta;

	/**
	 * Creates GLFW devices.
	 *
	 * @param displayWidth The window width in pixels.
	 * @param displayHeight The window height in pixels.
	 * @param displayTitle The window title.
	 * @param displayVSync If the window will use vSync..
	 * @param displayAntialiasing If OpenGL will use altialiasing.
	 * @param displaySamples How many MFAA samples should be done before swapping display buffers. Zero disables multisampling. GLFW_DONT_CARE means no preference.
	 * @param displayFullscreen If the window will start fullscreen.
	 */
	public FlounderDevices(int displayWidth, int displayHeight, String displayTitle, boolean displayVSync, boolean displayAntialiasing, int displaySamples, boolean displayFullscreen) {
		if (!initialized) {
			glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));
			display = new DeviceDisplay(displayWidth, displayHeight, displayTitle, displayVSync, displayAntialiasing, displaySamples, displayFullscreen);
			keyboard = new DeviceKeyboard();
			mouse = new DeviceMouse();
			joysticks = new DeviceJoysticks();
			sound = new DeviceSound();

			currentFrameTime = 0.0f;
			lastFrameTime = 0.0f;
			delta = 0.0f;

			// Logs OpenGL version info.
			FlounderLogger.log("Number of Cores: " + Runtime.getRuntime().availableProcessors());
			FlounderLogger.log("OpenGL Version: " + glGetString(GL_VERSION));
			initialized = true;
		}
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
		Runtime runtime = Runtime.getRuntime();
		NumberFormat format = NumberFormat.getInstance();

		StringBuilder sb = new StringBuilder();
		long maxMemory = runtime.maxMemory();
		long allocatedMemory = runtime.totalMemory();
		long freeMemory = runtime.freeMemory();

		sb.append("Free memory: " + format.format(freeMemory / 1024) + "\n");
		sb.append("Allocated memory: " + format.format(allocatedMemory / 1024) + "\n");
		sb.append("Max memory: " + format.format(maxMemory / 1024) + "\n");
		sb.append("Total free memory: " + format.format((freeMemory + maxMemory - allocatedMemory) / 1024) + "\n");
		return sb;
	}

	/**
	 * Runs the engines main device loop. Call {@link #dispose()} right after running to close the device system.
	 */
	@Override
	public void run() {
//		while (initialized && FlounderDevices.getDisplay().isOpen()) {
		// Updates static delta and times.
		currentFrameTime = FlounderDevices.getDisplay().getTime() / 1000.0f;
		delta = currentFrameTime - lastFrameTime;
		lastFrameTime = currentFrameTime;

		display.pollEvents();
		joysticks.update(delta);
		keyboard.update(delta);
		mouse.update(delta);
		sound.update(delta);
//		}
	}

	/**
	 * Updates the after frame device systems.
	 */
	public void swapToDisplay() {
		display.swapBuffers();
	}

	/**
	 * Closes the GLFW devices, do not use device objects after calling this.
	 */
	public void dispose() {
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
