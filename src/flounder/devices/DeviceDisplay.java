package flounder.devices;

import flounder.engine.*;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import javax.imageio.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.nio.*;
import java.util.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * Manages the creation, updating and destruction of the display, as well as timing and frame times.
 */
public class DeviceDisplay {
	private final GLFWWindowCloseCallback callbackWindowClose;
	private final GLFWWindowFocusCallback callbackWindowFocus;
	private final GLFWWindowPosCallback callbackWindowPos;
	private final GLFWWindowSizeCallback callbackWindowSize;
	private final GLFWFramebufferSizeCallback callbackFramebufferSize;

	private long window;
	private int width;
	private int height;
	private String title;
	private boolean enableVSync;
	private boolean antialiasing;
	private int samples;
	private boolean fullscreen;
	private int positionX, positionY;
	private boolean inFocus;
	private boolean closeRequested;

	/**
	 * Creates a new GLFW window.
	 *
	 * @param displayWidth The window width in pixels.
	 * @param displayHeight The window height in pixels.
	 * @param displayTitle The window title.
	 * @param displayVSync If the window will use vSync..
	 * @param antialiasing If OpenGL will use altialiasing.
	 * @param samples How many MFAA samples should be done before swapping buffers. Zero disables multisampling. GLFW_DONT_CARE means no preference.
	 * @param displayFullscreen If the window will start fullscreen.
	 */
	protected DeviceDisplay(final int displayWidth, final int displayHeight, final String displayTitle, final boolean displayVSync, final boolean antialiasing, final int samples, final boolean displayFullscreen) {
		this.width = displayWidth;
		this.height = displayHeight;
		this.title = displayTitle;
		this.enableVSync = displayVSync;
		this.antialiasing = antialiasing;
		this.fullscreen = displayFullscreen;
		this.samples = samples;
		inFocus = true;
		closeRequested = false;

		// Initialize the library.
		if (glfwInit() != GLFW_TRUE) {
			Logger.error("Could not init GLFW!");
			System.exit(-1);
		}

		// Configures the window.
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // The window will stay hidden until after creation.
		glfwWindowHint(GLFW_RESIZABLE, GL_FALSE); // TODO: Make true again!
		// glfwWindowHint(GLFW_RESIZABLE, displayFullscreen ? GL_FALSE : GL_TRUE); // The window will be resizable depending on if its createDisplay.
		glfwWindowHint(GLFW_SAMPLES, samples);
		glfwWindowHint(GLFW_REFRESH_RATE, GLFW_DONT_CARE); // Only enabled in fullscreen.

		// Gets the resolution of the primary monitor.
		final GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

		// Create a windowed mode window and its OpenGL context.
		window = glfwCreateWindow(displayWidth, displayHeight, displayTitle, NULL, NULL);

		// Sets the display to fullscreen or windowed.
		setFullscreen(displayFullscreen);

		// Gets any window errors.
		if (window == NULL) {
			Logger.error("Could not create the window!");
			glfwTerminate();
			System.exit(-1);
		}

		// Creates the OpenGL context.
		glfwMakeContextCurrent(window);

		// LWJGL will detect the context that is current in the current thread, creates the GLCapabilities instance and makes the OpenGL bindings available for use.
		GL.createCapabilities();

		// Gets any OpenGL errors.
		final long glError = glGetError();

		if (glError != GL_NO_ERROR) {
			Logger.error("OpenGL Error: " + glError);
			glfwDestroyWindow(window);
			glfwTerminate();
			System.exit(-1);
		}

		// Enables VSync if requested.
		setEnableVSync(displayVSync);

		// Centers the window position.
		glfwSetWindowPos(window, (positionX = (vidmode.width() - displayWidth) / 2), (positionY = (vidmode.height() - displayHeight) / 2));

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
			public void invoke(long window, int focused) {
				inFocus = focused == GL_TRUE;
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
		final String name = Calendar.getInstance().get(Calendar.MONTH) + 1 + "." + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "." + Calendar.getInstance().get(Calendar.HOUR) + "." + Calendar.getInstance().get(Calendar.MINUTE) + "." + (Calendar.getInstance().get(Calendar.SECOND) + 1);
		final File saveDirectory = new File("screenshots");

		if (!saveDirectory.exists()) {
			try {
				saveDirectory.mkdir();
			} catch (SecurityException e) {
				Logger.error("The screenshot directory could not be created.");
				e.printStackTrace();
				return;
			}
		}

		File file = new File(saveDirectory + "/" + name + ".png"); // The file to save the pixels too.
		String format = "png"; // "PNG" or "JPG".

		Logger.log("Taking screenshot and outputting it to " + file.getAbsolutePath());

		// Tries to create image.
		try {
			ImageIO.write(updateBufferedImage(), format, file);
		} catch (Exception e) {
			Logger.error("Failed to take screenshot.");
			e.printStackTrace();
		}
	}

	private BufferedImage updateBufferedImage() { // TODO Update a BufferedImage!
		ByteBuffer buffer = BufferUtils.createByteBuffer(getWidth() * getHeight() * 3);
		GL11.glReadPixels(0, 0, getWidth(), getHeight(), GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, buffer);
		BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);

		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				int i = (x + getWidth() * y) * 3;
				int r = buffer.get(i) & 0xFF;
				int g = buffer.get(i + 1) & 0xFF;
				int b = buffer.get(i + 2) & 0xFF;
				int rgb = ((r & 0x0ff) << 16) | ((g & 0x0ff) << 8) | (b & 0x0ff);
				image.setRGB(x, y, rgb);
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
	public boolean isEnableVSync() {
		return enableVSync;
	}

	/**
	 * Set the display to use VSync or not.
	 *
	 * @param enableVSync Weather or not to use vSync.
	 */
	public void setEnableVSync(final boolean enableVSync) {
		this.enableVSync = enableVSync;
		glfwSwapInterval(this.enableVSync ? 1 : 0);
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
	public void setAntialiasing(final boolean antialiasing) {
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
	public void setSamples(final int samples) {
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
	public void setFullscreen(final boolean fullscreen) {
		this.fullscreen = fullscreen;
		// TODO: Put display in fullscreen!
		glfwWindowHint(GLFW_RESIZABLE, this.fullscreen ? GL_FALSE : GL_TRUE);
	}

	/**
	 * @return The x pos of the display in pixels.
	 */
	public int getXPos() {
		return positionX;
	}

	/**
	 * @return The y pos of the display in pixels.
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
		return !closeRequested && glfwWindowShouldClose(window) != GL_TRUE;
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
		callbackWindowClose.release();
		callbackWindowFocus.release();
		callbackWindowPos.release();
		callbackWindowSize.release();
		callbackFramebufferSize.release();
		glfwDestroyWindow(window);
		glfwTerminate();
	}
}
