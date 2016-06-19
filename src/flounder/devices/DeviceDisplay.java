package flounder.devices;

import flounder.engine.*;
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
 * Manages the creation, updating and destruction of the display.
 */
public class DeviceDisplay implements IModule {
	private int windowWidth;
	private int windowHeight;
	private String title;
	private boolean vsync;
	private boolean antialiasing;
	private int samples;
	private boolean fullscreen;

	private long window;
	private boolean closed;
	private boolean focused;
	private int windowPosX;
	private int windowPosY;

	private GLFWWindowCloseCallback callbackWindowClose;
	private GLFWWindowFocusCallback callbackWindowFocus;
	private GLFWWindowPosCallback callbackWindowPos;
	private GLFWWindowSizeCallback callbackWindowSize;
	private GLFWFramebufferSizeCallback callbackFramebufferSize;

	/**
	 * Creates a new GLFW display.
	 *
	 * @param width The window width in pixels.
	 * @param height The window height in pixels.
	 * @param title The window title.
	 * @param vsync If the window will use vSync..
	 * @param antialiasing If OpenGL will use antialiasing.
	 * @param samples How many MFAA samples should be done before swapping buffers. Zero disables multisampling. GLFW_DONT_CARE means no preference.
	 * @param fullscreen If the window will start fullscreen.
	 */
	protected DeviceDisplay(int width, int height, String title, boolean vsync, boolean antialiasing, int samples, boolean fullscreen) {
		this.windowWidth = width;
		this.windowHeight = height;
		this.title = title;
		this.vsync = vsync;
		this.antialiasing = antialiasing;
		this.samples = samples;
		this.fullscreen = fullscreen;
	}

	@Override
	public void init() {
		// Initialize the GLFW library.
		if (!glfwInit()) {
			FlounderEngine.getLogger().error("Could not init GLFW!");
			System.exit(-1);
		}

		// Configures the window.
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // The window will stay hidden until after creation.
		glfwWindowHint(GLFW_RESIZABLE, fullscreen ? GL_FALSE : GL_TRUE); // The window will be resizable depending on if its createDisplay.
		glfwWindowHint(GLFW_SAMPLES, samples);
		glfwWindowHint(GLFW_REFRESH_RATE, GLFW_DONT_CARE); // Only enabled in fullscreen.

		// Gets the resolution of the primary monitor.
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

		if (fullscreen) {
			windowWidth = vidmode.width();
			windowHeight = vidmode.height();
		}

		// Create a windowed mode window and its OpenGL context.
		window = glfwCreateWindow(windowWidth, windowHeight, title, fullscreen ? glfwGetPrimaryMonitor() : NULL, NULL);

		// Sets the display to fullscreen or windowed.
		glfwWindowHint(GLFW_RESIZABLE, fullscreen ? GL_FALSE : GL_TRUE);

		// Gets any window errors.
		if (window == NULL) {
			FlounderEngine.getLogger().error("Could not create the window!");
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
			FlounderEngine.getLogger().error("OpenGL Error: " + glError);
			glfwDestroyWindow(window);
			glfwTerminate();
			System.exit(-1);
		}

		// Enables VSync if requested.
		glfwSwapInterval(vsync ? 1 : 0);

		// Centers the window position.
		if (!this.fullscreen) {
			glfwSetWindowPos(window, (windowPosX = (vidmode.width() - windowWidth) / 2), (windowPosY = (vidmode.height() - windowHeight) / 2));
		}

		// Shows the OpenGl window.
		glfwShowWindow(window);

		// Sets the displays callbacks.
		glfwSetWindowCloseCallback(window, callbackWindowClose = new GLFWWindowCloseCallback() {
			@Override
			public void invoke(long window) {
				closed = true;
			}
		});

		glfwSetWindowFocusCallback(window, callbackWindowFocus = new GLFWWindowFocusCallback() {
			@Override
			public void invoke(long window, boolean focus) {
				focused = focus;
			}
		});

		glfwSetWindowPosCallback(window, callbackWindowPos = new GLFWWindowPosCallback() {
			@Override
			public void invoke(long window, int xpos, int ypos) {
				windowPosX = xpos;
				windowPosY = ypos;
			}
		});

		glfwSetWindowSizeCallback(window, callbackWindowSize = new GLFWWindowSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				windowWidth = width;
				windowHeight = height;
			}
		});

		glfwSetFramebufferSizeCallback(window, callbackFramebufferSize = new GLFWFramebufferSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				glViewport(0, 0, width, height);
			}
		});
	}

	@Override
	public void update() {
		// Polls for window events. The key callback will only be invoked during this call.
		glfwPollEvents();
	}

	/**
	 * Updates the display image by swapping the colour buffers.
	 */
	public void swapBuffers() {
		glfwSwapBuffers(window);
	}

	@Override
	public void profile() {
		FlounderEngine.getProfiler().add("Display", "Width", windowWidth);
		FlounderEngine.getProfiler().add("Display", "Height", windowHeight);
		FlounderEngine.getProfiler().add("Display", "Title", title);
		FlounderEngine.getProfiler().add("Display", "VSync", vsync);
		FlounderEngine.getProfiler().add("Display", "Antialiasing", antialiasing);
		FlounderEngine.getProfiler().add("Display", "Samples", samples);
		FlounderEngine.getProfiler().add("Display", "Fullscreen", fullscreen);

		FlounderEngine.getProfiler().add("Display", "Closed", closed);
		FlounderEngine.getProfiler().add("Display", "Focused", focused);
		FlounderEngine.getProfiler().add("Display", "Window Pos.X", windowPosX);
		FlounderEngine.getProfiler().add("Display", "Window Pos.Y", windowPosY);
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
				FlounderEngine.getLogger().error("The screenshot directory could not be created.");
				FlounderEngine.getLogger().exception(e);
				return;
			}
		}

		File file = new File(saveDirectory + "/" + name + ".png"); // The file to save the pixels too.
		String format = "png"; // "PNG" or "JPG".

		FlounderEngine.getLogger().log("Taking screenshot and outputting it to " + file.getAbsolutePath());

		// Tries to create image.
		try {
			ImageIO.write(createBufferedImage(), format, file);
		} catch (Exception e) {
			FlounderEngine.getLogger().error("Failed to take screenshot.");
			FlounderEngine.getLogger().exception(e);
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
	 * Sets if the operating systems cursor is hidden whilst in the display.
	 *
	 * @param hidden If the cursor should be hidden.
	 */
	public void setCursorHidden(boolean hidden) {
		glfwSetInputMode(window, GLFW_CURSOR, hidden ? GLFW_CURSOR_HIDDEN : GLFW_CURSOR_NORMAL);
	}

	/**
	 * Gets the width of the display in pixels.
	 *
	 * @return The width of the display.
	 */
	public int getWidth() {
		return windowWidth;
	}

	/**
	 * Gets the height of the display in pixels.
	 *
	 * @return The height of the display.
	 */
	public int getHeight() {
		return windowHeight;
	}

	/**
	 * Gets the aspect ratio between the displays width and height.
	 *
	 * @return The aspect ratio.
	 */
	public float getAspectRatio() {
		return ((float) getWidth()) / ((float) getHeight());
	}

	/**
	 * Gets the window's title.
	 *
	 * @return The window's title.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * gets if the display is using vSync.
	 *
	 * @return If VSync is enabled.
	 */
	public boolean isVSync() {
		return vsync;
	}

	/**
	 * Sets the display to use VSync or not.
	 *
	 * @param vsync Weather or not to use vSync.
	 */
	public void setVSync(boolean vsync) {
		this.vsync = vsync;
		glfwSwapInterval(vsync ? 1 : 0);
	}

	/**
	 * Gets if the display requests antialiased images.
	 *
	 * @return If using antialiased images.
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
	 * Gets how many MFAA samples should be done before swapping buffers.
	 *
	 * @return Amount of MFAA samples.
	 */
	public int getSamples() {
		return samples;
	}

	/**
	 * Gets how many MFAA samples should be done before swapping buffers. Zero disables multisampling. GLFW_DONT_CARE means no preference.
	 *
	 * @param samples The amount of MFSS samples.
	 */
	public void setSamples(int samples) {
		this.samples = samples;
		glfwWindowHint(GLFW_SAMPLES, samples);
	}

	/**
	 * Gets weather the display is fullscreen or not.
	 *
	 * @return Fullscreen or windowed.
	 */
	public boolean isFullscreen() {
		return fullscreen;
	}

	/**
	 * Sets the display to be fullscreen or windowed.
	 *
	 * @param fullscreen Weather or not to be fullscreen.
	 */
	public void setFullscreen(boolean fullscreen) {
		if (this.fullscreen == fullscreen) {
			return;
		}

		this.fullscreen = fullscreen;
		FlounderEngine.getLogger().log(fullscreen ? "Display is going fullscreen." : "Display is going windowed.");
		glfwWindowHint(GLFW_RESIZABLE, fullscreen ? GL_FALSE : GL_TRUE);
		// TODO: MAKE FULLSCREEN WORK!!!
	}

	/**
	 * Gets the current GLFW window.
	 *
	 * @return The current GLFW window.
	 */
	public long getWindow() {
		return window;
	}

	/**
	 * Gets if the GLFW display is closed.
	 *
	 * @return If the GLFW display is closed.
	 */
	public boolean isClosed() {
		return closed;
	}

	/**
	 * Gets if the GLFW display is selected.
	 *
	 * @return If the GLFW display is selected.
	 */
	public boolean isFocused() {
		return focused;
	}

	/**
	 * Gets the windows Y position of the display in pixels.
	 *
	 * @return The windows x position.
	 */
	public int getWindowXPos() {
		return windowPosX;
	}

	/**
	 * Gets the windows Y position of the display in pixels.
	 *
	 * @return The windows Y position.
	 */
	public int getWindowYPos() {
		return windowPosY;
	}

	@Override
	public void dispose() {
		callbackWindowClose.free();
		callbackWindowFocus.free();
		callbackWindowPos.free();
		callbackWindowSize.free();
		callbackFramebufferSize.free();
		glfwDestroyWindow(window);
		glfwTerminate();
	}
}
