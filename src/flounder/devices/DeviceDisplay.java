package flounder.devices;

import flounder.engine.*;
import flounder.profiling.*;
import org.lwjgl.*;
import org.lwjgl.glfw.*;

import javax.imageio.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.nio.*;
import java.util.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * Manages the creation, updating and destruction of the display, as well as timing and frame times.
 */
public class DeviceDisplay {
	private GLFWWindowCloseCallback callbackWindowClose;
	private GLFWWindowFocusCallback callbackWindowFocus;
	private GLFWWindowPosCallback callbackWindowPos;
	private GLFWWindowSizeCallback callbackWindowSize;
	private GLFWFramebufferSizeCallback callbackFramebufferSize;

	private long window;
	private int width;
	private int height;
	private String title;
	private boolean vsyncEnabled;
	private boolean antialiasing;
	private int samples;
	private boolean fullscreen;
	private int positionX, positionY;
	private boolean inFocus;
	private boolean closeRequested;

	/**
	 * Creates a new GLFW window.
	 *
	 * @param startWidth The window width in pixels.
	 * @param startHeight The window height in pixels.
	 * @param title The window title.
	 * @param vsync If the window will use vSync..
	 * @param antialiasing If OpenGL will use altialiasing.
	 * @param samples How many MFAA samples should be done before swapping buffers. Zero disables multisampling. GLFW_DONT_CARE means no preference.
	 * @param fullscreen If the window will start fullscreen.
	 */
	protected DeviceDisplay(int startWidth, int startHeight, String title, boolean vsync, boolean antialiasing, int samples, boolean fullscreen) {
		this.width = startWidth;
		this.height = startHeight;
		this.title = title;
		this.vsyncEnabled = vsync;
		this.antialiasing = antialiasing;
		this.fullscreen = fullscreen;
		this.samples = samples;
		this.inFocus = true;
		this.closeRequested = false;

		// Initialize the GLFW library.
		if (!glfwInit()) {
			FlounderLogger.error("Could not init GLFW!");
			System.exit(-1);
		}

		// Configures the window.
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // The window will stay hidden until after creation.
		glfwWindowHint(GLFW_RESIZABLE, this.fullscreen ? GL_FALSE : GL_TRUE); // The window will be resizable depending on if its createDisplay.
		glfwWindowHint(GLFW_SAMPLES, this.samples);
		glfwWindowHint(GLFW_REFRESH_RATE, GLFW_DONT_CARE); // Only enabled in fullscreen.

		// Gets the resolution of the primary monitor.
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

		if (fullscreen) {
			this.width = vidmode.width();
			this.height = vidmode.height();
		}

		// Create a windowed mode window and its OpenGL context.
		window = glfwCreateWindow(this.width, this.height, this.title, this.fullscreen ? glfwGetPrimaryMonitor() : NULL, NULL);

		// Sets the display to fullscreen or windowed.
		setFullscreen(this.fullscreen);

		// Gets any window errors.
		if (window == NULL) {
			FlounderLogger.error("Could not create the window!");
			glfwTerminate();
			System.exit(-1);
		}

		// Creates the OpenGL context.
		glfwMakeContextCurrent(window);

		// LWJGL will detect the context that is current in the current thread, creates the GLCapabilities instance and makes the OpenGL bindings available for use.
		createCapabilities();

		// Gets any OpenGL errors.
		long glError = glGetError();

		if (glError != GL_NO_ERROR) {
			FlounderLogger.error("OpenGL Error: " + glError);
			glfwDestroyWindow(window);
			glfwTerminate();
			System.exit(-1);
		}

		// Enables VSync if requested.
		setVsyncEnabled(vsync);

		// Centers the window position.
		if (!this.fullscreen) {
			glfwSetWindowPos(window, (positionX = (vidmode.width() - this.width) / 2), (positionY = (vidmode.height() - this.height) / 2));
		}

		// Shows the OpenGl window.
		glfwShowWindow(window);

		// Sets the displays callbacks.
		glfwSetWindowCloseCallback(window, callbackWindowClose = new GLFWWindowCloseCallback() {
			@Override
			public void invoke(long window) {
				closeRequested = true;
			}
		});

		glfwSetWindowFocusCallback(window, callbackWindowFocus = new GLFWWindowFocusCallback() {
			@Override
			public void invoke(long window, boolean focused) {
				inFocus = focused;
			}
		});

		glfwSetWindowPosCallback(window, callbackWindowPos = new GLFWWindowPosCallback() {
			@Override
			public void invoke(long window, int xpos, int ypos) {
				positionX = xpos;
				positionY = ypos;
			}
		});

		glfwSetWindowSizeCallback(window, callbackWindowSize = new GLFWWindowSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				DeviceDisplay.this.width = width;
				DeviceDisplay.this.height = height;
			}
		});

		glfwSetFramebufferSizeCallback(window, callbackFramebufferSize = new GLFWFramebufferSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				glViewport(0, 0, width, height);
			}
		});
	}

	/**
	 * Polls for window events. The key callback will only be invoked during this call.
	 */
	protected void pollEvents() {
		glfwPollEvents();
		addProfileValues();
	}

	private void addProfileValues() {
		if (FlounderProfiler.isOpen()) {
			FlounderProfiler.add("Display", "Width", width);
			FlounderProfiler.add("Display", "Height", height);
			FlounderProfiler.add("Display", "Title", title);
			FlounderProfiler.add("Display", "Enable VSync", vsyncEnabled);
			FlounderProfiler.add("Display", "Antialiasing", antialiasing);
			FlounderProfiler.add("Display", "Samples", samples);
			FlounderProfiler.add("Display", "Fullscreen", fullscreen);
			FlounderProfiler.add("Display", "Position X", positionX);
			FlounderProfiler.add("Display", "Position Y", positionY);
			FlounderProfiler.add("Display", "In Focus", inFocus);
			FlounderProfiler.add("Display", "Close Requested", closeRequested);
		}
	}

	/**
	 * Updates the display image by swaping the colour buffers.
	 */
	protected void swapBuffers() {
		glfwSwapBuffers(window);
	}

	/**
	 * Takes a screenshot of the current image of the display and saves it into the screenshots folder a png image.
	 */
	public void screenshot() {
		// Tries to create an image, otherwise throws an exception.
		String name = Calendar.getInstance().get(Calendar.MONTH) + 1 + "." + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "." + Calendar.getInstance().get(Calendar.HOUR) + "." + Calendar.getInstance().get(Calendar.MINUTE) + "." + (Calendar.getInstance().get(Calendar.SECOND) + 1);
		File saveDirectory = new File("screenshots");

		if (!saveDirectory.exists()) {
			try {
				saveDirectory.mkdir();
			} catch (SecurityException e) {
				FlounderLogger.error("The screenshot directory could not be created.");
				FlounderLogger.exception(e);
				return;
			}
		}

		File file = new File(saveDirectory + "/" + name + ".png"); // The file to save the pixels too.
		String format = "png"; // "PNG" or "JPG".

		FlounderLogger.log("Taking screenshot and outputting it to " + file.getAbsolutePath());

		// Tries to create image.
		try {
			ImageIO.write(createBufferedImage(), format, file);
		} catch (Exception e) {
			FlounderLogger.error("Failed to take screenshot.");
			FlounderLogger.exception(e);
		}
	}

	/**
	 * Creates a buffered image from the OpenGL pixel buffer.
	 *
	 * @return A new buffered image containing the displays data.
	 */
	private BufferedImage createBufferedImage() {
		// Creates a new buffer and stores the displays data into it.
		ByteBuffer buffer = BufferUtils.createByteBuffer(getWidth() * getHeight() * 3);
		glReadPixels(0, 0, getWidth(), getHeight(), GL_RGB, GL_UNSIGNED_BYTE, buffer);
		BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);

		// Transfers the data from the buffer into the image. This requires bit shifts to get the components data.
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				int i = (x + getWidth() * y) * 3;
				image.setRGB(x, y, (((buffer.get(i) & 0xFF) & 0x0ff) << 16) | (((buffer.get(i + 1) & 0xFF) & 0x0ff) << 8) | ((buffer.get(i + 2) & 0xFF) & 0x0ff));
			}
		}

		// Creates the transformation direction (horizontal).
		AffineTransform at = AffineTransform.getScaleInstance(1, -1);
		at.translate(0, -image.getHeight(null));

		// Applies transformation.
		AffineTransformOp opRotated = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		return opRotated.filter(image, null);
	}

	/**
	 * @return The width of the display in pixels.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return The height of the display in pixels.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Sets if the operating systems cursor is hidden whilst in the display.
	 *
	 * @param hidden If the cursor should be hidden.
	 */
	public void setCursorHidden(boolean hidden) {
		glfwSetInputMode(window, GLFW_CURSOR, hidden ? GLFW_CURSOR_HIDDEN : GLFW_CURSOR_NORMAL);
	}

	/**
	 * @return The current GLFW window.
	 */
	public long getWindow() {
		return window;
	}

	/**
	 * @return The aspect ratio between the displays width and height.
	 */
	public float getAspectRatio() {
		return ((float) width) / ((float) height);
	}

	/**
	 * @return The window's title.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return If the display is using vSync.
	 */
	public boolean isVsyncEnabled() {
		return vsyncEnabled;
	}

	/**
	 * Set the display to use VSync or not.
	 *
	 * @param vsyncEnabled Weather or not to use vSync.
	 */
	public void setVsyncEnabled(boolean vsyncEnabled) {
		this.vsyncEnabled = vsyncEnabled;
		glfwSwapInterval(this.vsyncEnabled ? 1 : 0);
	}

	/**
	 * @return If the display requests antialiased images.
	 */
	public boolean isAntialiasing() {
		return antialiasing;
	}

	/**
	 * Requests the display to antialias.
	 *
	 * @param antialiasing If the display should antialias.
	 */
	public void setAntialiasing(boolean antialiasing) {
		this.antialiasing = antialiasing;
	}

	/**
	 * @return How many MFAA samples should be done before swapping buffers.
	 */
	public int getSamples() {
		return samples;
	}

	/**
	 * How many MFAA samples should be done before swapping buffers. Zero disables multisampling. GLFW_DONT_CARE means no preference.
	 *
	 * @param samples The amount of MFSS samples to be used in the window.
	 */
	public void setSamples(int samples) {
		this.samples = samples;
		glfwWindowHint(GLFW_SAMPLES, samples);
	}

	/**
	 * @return Weather the display is fullscreen or not.
	 */
	public boolean isFullscreen() {
		return fullscreen;
	}

	/**
	 * Set the display to fullscreen or windowed.
	 *
	 * @param fullscreen Weather or not to be fullscreen.
	 */
	public void setFullscreen(boolean fullscreen) {
		FlounderLogger.log(this.fullscreen && !fullscreen ? "Display going windowed." : !this.fullscreen && fullscreen ? "Display going fullscreen." : "");
		this.fullscreen = fullscreen;
		glfwWindowHint(GLFW_RESIZABLE, this.fullscreen ? GL_FALSE : GL_TRUE);
		// TODO: MAKE FULLSCREEN WORK!!!
	}

	/**
	 * @return The x posiiton of the display in pixels.
	 */
	public int getXPos() {
		return positionX;
	}

	/**
	 * @return The y posiiton of the display in pixels.
	 */
	public int getYPos() {
		return positionY;
	}

	/**
	 * @return If the GLFW display is selected.
	 */
	public boolean isFocused() {
		return inFocus;
	}

	/**
	 * @return If the GLFW display is open or if close has not been requested.
	 */
	public boolean isOpen() {
		return !closeRequested && !glfwWindowShouldClose(window);
	}

	/**
	 * Indicates that the game has been requested to close. At the end of the current frame the main game loop will exit.
	 */
	public void requestClose() {
		closeRequested = true;
	}

	/**
	 * @return The current GLFW time time in seconds (by running glfwGetTime() * 1000.0f).
	 */
	public float getTime() {
		return (float) (glfwGetTime() * 1000.0f);
	}

	/**
	 * Closes the GLFW display, do not renderObjects after calling this.
	 */
	protected void dispose() {
		callbackWindowClose.free();
		callbackWindowFocus.free();
		callbackWindowPos.free();
		callbackWindowSize.free();
		callbackFramebufferSize.free();
		glfwDestroyWindow(window);
		glfwTerminate();
	}
}
