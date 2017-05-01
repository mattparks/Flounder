package flounder.devices;

import flounder.framework.*;
import flounder.logger.*;
import flounder.platform.*;

import javax.imageio.*;
import java.awt.image.*;
import java.io.*;
import java.nio.*;
import java.util.*;

/**
 * A module used for the creation, updating and destruction of the display.
 */
public class FlounderDisplay extends Module {
	/**
	 * Creates a new GLFW display manager.
	 */
	public FlounderDisplay() {
		super(FlounderPlatform.class, FlounderDisplaySync.class);
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
	}

	@Handler.Function(Handler.FLAG_UPDATE_ALWAYS)
	public void update() {
	}

	/**
	 * Updates the display image by swapping the colour buffers.
	 */
	public void swapBuffers() {
	}

	@Handler.Function(Handler.FLAG_PROFILE)
	public void profile() {
	}

	/**
	 * Takes a screenshot of the current image of the display and saves it into the screenshots folder a png image.
	 */
	public void screenshot() {
		// Tries to create an image, otherwise throws an exception.
		String name = Calendar.getInstance().get(Calendar.MONTH) + 1 + "." + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "." + Calendar.getInstance().get(Calendar.HOUR) + "." + Calendar.getInstance().get(Calendar.MINUTE) + "." + (Calendar.getInstance().get(Calendar.SECOND) + 1);
		File saveDirectory = new File(Framework.getRoamingFolder().getPath(), "screenshots");

		if (!saveDirectory.exists()) {
			try {
				if (!saveDirectory.mkdir()) {
					FlounderLogger.get().error("The screenshot directory could not be created.");
				}
			} catch (SecurityException e) {
				FlounderLogger.get().error("The screenshot directory could not be created.");
				FlounderLogger.get().exception(e);
				return;
			}
		}

		File file = new File(saveDirectory + "/" + name + ".png"); // The file to save the pixels too.
		String format = "png"; // "PNG" or "JPG".

		FlounderLogger.get().log("Taking screenshot and outputting it to " + file.getAbsolutePath());

		// Tries to create image.
		try {
			ImageIO.write(getImage(null, null), format, file);
		} catch (Exception e) {
			FlounderLogger.get().error("Failed to take screenshot.");
			FlounderLogger.get().exception(e);
		}
	}

	/**
	 * Creates a buffered image from the OpenGL pixel buffer.
	 *
	 * @param destination The destination BufferedImage to store in, if null a new one will be created.
	 * @param buffer The buffer to store OpenGL data into, if null a new one will be created.
	 *
	 * @return A new buffered image containing the displays data.
	 */
	public BufferedImage getImage(BufferedImage destination, ByteBuffer buffer) {
		return null;
	}

	/**
	 * Gets the width of the display in pixels.
	 *
	 * @return The width of the display.
	 */
	public int getWidth() {
		return 0;
	}

	public int getWindowWidth() {
		return 0;
	}

	/**
	 * Gets the height of the display in pixels.
	 *
	 * @return The height of the display.
	 */
	public int getHeight() {
		return 0;
	}

	public int getWindowHeight() {
		return 0;
	}

	public void setWindowSize(int width, int height) {
	}

	/**
	 * Gets the aspect ratio between the displays width and height.
	 *
	 * @return The aspect ratio.
	 */
	public float getAspectRatio() {
		return 1;
	}

	/**
	 * Gets the window's title.
	 *
	 * @return The window's title.
	 */
	public String getTitle() {
		return "NULL";
	}

	/**
	 * gets if the display is using vSync.
	 *
	 * @return If VSync is enabled.
	 */
	public boolean isVSync() {
		return false;
	}

	/**
	 * Sets the display to use VSync or not.
	 *
	 * @param vsync Weather or not to use vSync.
	 */
	public void setVSync(boolean vsync) {
	}

	/**
	 * Gets if the display requests antialiased images.
	 *
	 * @return If using antialiased images.
	 */
	public boolean isAntialiasing() {
		return false;
	}

	/**
	 * Requests the display to antialias.
	 *
	 * @param antialiasing If the display should antialias.
	 */
	public void setAntialiasing(boolean antialiasing) {
	}

	/**
	 * Gets how many MFAA samples should be done before swapping buffers.
	 *
	 * @return Amount of MFAA samples.
	 */
	public int getSamples() {
		return 0;
	}

	/**
	 * Gets how many MFAA samples should be done before swapping buffers. Zero disables multisampling. GLFW_DONT_CARE means no preference.
	 *
	 * @param samples The amount of MFSS samples.
	 */
	public void setSamples(int samples) {
	}

	/**
	 * Gets weather the display is fullscreen or not.
	 *
	 * @return Fullscreen or windowed.
	 */
	public boolean isFullscreen() {
		return false;
	}

	/**
	 * Sets the display to be fullscreen or windowed.
	 *
	 * @param fullscreen Weather or not to be fullscreen.
	 */
	public void setFullscreen(boolean fullscreen) {
	}

	/**
	 * Gets the current GLFW window.
	 *
	 * @return The current GLFW window.
	 */
	public long getWindow() {
		return 0;
	}

	/**
	 * Gets if the GLFW display is closed.
	 *
	 * @return If the GLFW display is closed.
	 */
	public boolean isClosed() {
		return false;
	}

	/**
	 * Gets if the GLFW display is selected.
	 *
	 * @return If the GLFW display is selected.
	 */
	public boolean isFocused() {
		return true;
	}

	/**
	 * Gets the windows Y position of the display in pixels.
	 *
	 * @return The windows x position.
	 */
	public int getWindowXPos() {
		return 0;
	}

	/**
	 * Gets the windows Y position of the display in pixels.
	 *
	 * @return The windows Y position.
	 */
	public int getWindowYPos() {
		return 0;
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
	}

	@Module.Instance
	public static FlounderDisplay get() {
		return (FlounderDisplay) Framework.getInstance(FlounderDisplay.class);
	}

	@Module.TabName
	public static String getTab() {
		return "Display";
	}
}
