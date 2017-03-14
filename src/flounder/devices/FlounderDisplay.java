package flounder.devices;

import flounder.fbos.*;
import flounder.framework.*;
import flounder.logger.*;
import flounder.profiling.*;
import flounder.resources.*;
import org.lwjgl.*;
import org.lwjgl.glfw.*;

import javax.imageio.*;
import java.awt.image.*;
import java.io.*;
import java.nio.*;
import java.util.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * A module used for the creation, updating and destruction of the display.
 */
public class FlounderDisplay extends Module {
	private static final FlounderDisplay INSTANCE = new FlounderDisplay();
	public static final String PROFILE_TAB_NAME = "Display";

	private int windowWidth;
	private int windowHeight;
	private int fullscreenWidth;
	private int fullscreenHeight;

	private String title;
	private MyFile[] icons;
	private boolean vsync;
	private boolean antialiasing;
	private int samples;
	private boolean fullscreen;
	private boolean hiddenDisplay;

	private long window;
	private boolean closed;
	private boolean focused;
	private int windowPosX;
	private int windowPosY;
	private boolean setup;

	private GLFWWindowCloseCallback callbackWindowClose;
	private GLFWWindowFocusCallback callbackWindowFocus;
	private GLFWWindowPosCallback callbackWindowPos;
	private GLFWWindowSizeCallback callbackWindowSize;
	private GLFWFramebufferSizeCallback callbackFramebufferSize;

	/**
	 * Creates a new GLFW display manager.
	 */
	public FlounderDisplay() {
		super(ModuleUpdate.UPDATE_ALWAYS, PROFILE_TAB_NAME, FlounderLogger.class, FlounderProfiler.class, FlounderDisplaySync.class);
	}

	/**
	 * A function called before initialization to configure the display.
	 *
	 * @param width The window width in pixels.
	 * @param height The window height in pixels.
	 * @param title The window title.
	 * @param icons A list of icons to load for the window.
	 * @param vsync If the window will use vSync..
	 * @param antialiasing If OpenGL will use antialiasing.
	 * @param samples How many MFAA samples should be done before swapping buffers. Zero disables multisampling. GLFW_DONT_CARE means no preference.
	 * @param fullscreen If the window will start fullscreen.
	 * @param hiddenDisplay If the display should be hidden on start, should be true when {@link FlounderDisplayJPanel} is being used.
	 */
	public static void setup(int width, int height, String title, MyFile[] icons, boolean vsync, boolean antialiasing, int samples, boolean fullscreen, boolean hiddenDisplay) {
		if (!INSTANCE.isInitialized()) {
			INSTANCE.windowWidth = width;
			INSTANCE.windowHeight = height;
			INSTANCE.title = title;
			INSTANCE.icons = icons;
			INSTANCE.vsync = vsync;
			INSTANCE.antialiasing = antialiasing;
			INSTANCE.samples = samples;
			INSTANCE.fullscreen = fullscreen;
			INSTANCE.hiddenDisplay = hiddenDisplay;
			INSTANCE.setup = true;
		} else {
			FlounderLogger.error("Flounder Display setup MUST be called before the INSTANCE is initialized.");
		}
	}

	@Override
	public void init() {
		if (!setup) {
			FlounderLogger.error("Flounder Display setup can be used to configure the display, default settings were set!");
			this.windowWidth = 1080;
			this.windowHeight = 720;
			this.title = "Testing 1";
			this.icons = new MyFile[]{new MyFile(MyFile.RES_FOLDER, "flounder.png")};
			this.vsync = true;
			this.antialiasing = true;
			this.samples = 0;
			this.fullscreen = false;
			this.setup = true;
		}

		// Initialize the GLFW library.
		if (!glfwInit()) {
			FlounderLogger.error("Could not init GLFW!");
			System.exit(-1);
		}

		// Gets the video mode from the primary monitor.
		long monitor = glfwGetPrimaryMonitor();
		GLFWVidMode mode = glfwGetVideoMode(monitor);

		// Configures the window.
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // The window will stay hidden until after creation.
		glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // The window will be resizable depending on if its createDisplay.

		// Use FBO antialiasing instead!
		//if (samples > 0) {
		//	glfwWindowHint(GLFW_SAMPLES, samples); // The number of MSAA samples to use.
		//}

		if (fullscreen && mode != null) {
			fullscreenWidth = mode.width();
			fullscreenHeight = mode.height();

			glfwWindowHint(GLFW_RED_BITS, mode.redBits());
			glfwWindowHint(GLFW_GREEN_BITS, mode.greenBits());
			glfwWindowHint(GLFW_BLUE_BITS, mode.blueBits());
			glfwWindowHint(GLFW_REFRESH_RATE, mode.refreshRate());
		}

		// Create a windowed mode window and its OpenGL context.
		window = glfwCreateWindow(fullscreen ? fullscreenWidth : windowWidth, fullscreen ? fullscreenHeight : windowHeight, title, fullscreen ? monitor : NULL, NULL);

		// Sets the display to fullscreen or windowed.
		focused = true;

		// Gets any window errors.
		if (window == NULL) {
			FlounderLogger.error("Could not create the window!");
			glfwTerminate();
			System.exit(-1);
		}

		// Creates the OpenGL context.
		glfwMakeContextCurrent(window);

		try {
			setWindowIcon();
		} catch (IOException e) {
			FlounderLogger.error("Could not load custom display image!");
			FlounderLogger.exception(e);
		}

		// LWJGL will detect the context that is current in the current thread, creates the GLCapabilities instance and makes the OpenGL bindings available for use.
		createCapabilities(true);

		// Gets any OpenGL errors.
		long glError = glGetError();

		if (glError != GL_NO_ERROR) {
			FlounderLogger.error("OpenGL Error: " + glError);
			glfwDestroyWindow(window);
			glfwTerminate();
			System.exit(-1);
		}

		// Enables VSync if requested.
		glfwSwapInterval(vsync ? 1 : 0);

		if (vsync) {
			Framework.setFpsLimit(60);
		}

		// Centres the window position.
		if (!fullscreen && mode != null) {
			glfwSetWindowPos(window, (windowPosX = (mode.width() - windowWidth) / 2), (windowPosY = (mode.height() - windowHeight) / 2));
		}

		// Shows the OpenGl window.
		if (hiddenDisplay) {
			glfwHideWindow(window);
		} else {
			glfwShowWindow(window);
		}

		// Sets the displays callbacks.
		glfwSetWindowCloseCallback(window, callbackWindowClose = new GLFWWindowCloseCallback() {
			@Override
			public void invoke(long window) {
				closed = true;
				Framework.requestClose();
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
				if (!fullscreen) {
					windowPosX = xpos;
					windowPosY = ypos;
				}
			}
		});

		glfwSetWindowSizeCallback(window, callbackWindowSize = new GLFWWindowSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				if (!fullscreen) {
					windowWidth = width;
					windowHeight = height;
				}
			}
		});

		glfwSetFramebufferSizeCallback(window, callbackFramebufferSize = new GLFWFramebufferSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				glViewport(0, 0, width, height);
			}
		});

		// System logs.
		FlounderLogger.log("");
		FlounderLogger.log("===== This is not an error message, it is a system info log. =====");
		FlounderLogger.log("Flounder Engine Version: " + Framework.getVersion().getVersion());
		FlounderLogger.log("Flounder Operating System: " + System.getProperty("os.name"));
		FlounderLogger.log("Flounder OpenGL Version: " + glGetString(GL_VERSION));
		FlounderLogger.log("Flounder OpenGL Vendor: " + glGetString(GL_VENDOR));
		FlounderLogger.log("Flounder Available Processors (cores): " + Runtime.getRuntime().availableProcessors());
		FlounderLogger.log("Flounder Free Memory (bytes): " + Runtime.getRuntime().freeMemory());
		FlounderLogger.log("Flounder Maximum Memory (bytes): " + (Runtime.getRuntime().maxMemory() == Long.MAX_VALUE ? "Unlimited" : Runtime.getRuntime().maxMemory()));
		FlounderLogger.log("Flounder Total Memory Available To JVM (bytes): " + Runtime.getRuntime().totalMemory());
		FlounderLogger.log("Flounder Maximum FBO Size: " + FBO.getMaxFBOSize());
		FlounderLogger.log("===== End of system info log. =====\n");
	}

	private void setWindowIcon() throws IOException {
		// Creates a GLFWImage Buffer,
		GLFWImage.Buffer images = GLFWImage.malloc(icons.length);
		for (int i = 0; i < icons.length; i++) {
			images.put(i, loadGLFWImage(icons[i])); // Stores a image into a slot.
		}

		// Loads the buffer into the window.
		glfwSetWindowIcon(window, images);
	}

	private GLFWImage loadGLFWImage(MyFile file) throws IOException {
		BufferedImage image = ImageIO.read(file.getInputStream());
		int width = image.getWidth();
		int height = image.getHeight();
		int[] pixels = new int[width * height];
		image.getRGB(0, 0, width, height, pixels, 0, width);

		// Converts image to RGBA format.
		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pixel = pixels[y * width + x];
				buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red.
				buffer.put((byte) ((pixel >> 8) & 0xFF)); // Green.
				buffer.put((byte) (pixel & 0xFF)); // Blue.
				buffer.put((byte) ((pixel >> 24) & 0xFF)); // Alpha.
			}
		}

		buffer.flip(); // This will flip the cursor image vertically.

		// Creates a GLFWImage.
		GLFWImage glfwImage = GLFWImage.create();
		glfwImage.width(width); // Setup the images' width.
		glfwImage.height(height); // Setup the images' height.
		glfwImage.pixels(buffer); // Pass image data.
		return glfwImage;
	}

	@Override
	public void update() {
		// Polls for window events. The key callback will only be invoked during this call.
		glfwPollEvents();
	}

	/**
	 * Updates the display image by swapping the colour buffers.
	 */
	public static void swapBuffers() {
		if (INSTANCE.window != 0) {
			glfwSwapBuffers(INSTANCE.window);
		}
	}

	@Override
	public void profile() {
		FlounderProfiler.add(PROFILE_TAB_NAME, "Width", windowWidth);
		FlounderProfiler.add(PROFILE_TAB_NAME, "Height", windowHeight);
		FlounderProfiler.add(PROFILE_TAB_NAME, "Title", title);
		FlounderProfiler.add(PROFILE_TAB_NAME, "VSync", vsync);
		FlounderProfiler.add(PROFILE_TAB_NAME, "Antialiasing", antialiasing);
		FlounderProfiler.add(PROFILE_TAB_NAME, "Samples", samples);
		FlounderProfiler.add(PROFILE_TAB_NAME, "Fullscreen", fullscreen);

		FlounderProfiler.add(PROFILE_TAB_NAME, "Closed", closed);
		FlounderProfiler.add(PROFILE_TAB_NAME, "Focused", focused);
		FlounderProfiler.add(PROFILE_TAB_NAME, "Window Pos.X", windowPosX);
		FlounderProfiler.add(PROFILE_TAB_NAME, "Window Pos.Y", windowPosY);
	}

	/**
	 * Takes a screenshot of the current image of the display and saves it into the screenshots folder a png image.
	 */
	public static void screenshot() {
		// Tries to create an image, otherwise throws an exception.
		String name = Calendar.getInstance().get(Calendar.MONTH) + 1 + "." + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "." + Calendar.getInstance().get(Calendar.HOUR) + "." + Calendar.getInstance().get(Calendar.MINUTE) + "." + (Calendar.getInstance().get(Calendar.SECOND) + 1);
		File saveDirectory = new File(Framework.getRoamingFolder().getPath(), "screenshots");

		if (!saveDirectory.exists()) {
			try {
				if (!saveDirectory.mkdir()) {
					FlounderLogger.error("The screenshot directory could not be created.");
				}
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
			ImageIO.write(getImage(null, null), format, file);
		} catch (Exception e) {
			FlounderLogger.error("Failed to take screenshot.");
			FlounderLogger.exception(e);
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
	public static BufferedImage getImage(BufferedImage destination, ByteBuffer buffer) {
		// Creates a new destination if it does not exist, or fixes a old one,
		if (destination == null || buffer == null || destination.getWidth() != getWidth() || destination.getHeight() != getHeight()) {
			destination = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
			buffer = BufferUtils.createByteBuffer(getWidth() * getHeight() * 4);
		}

		// Creates a new buffer and stores the displays data into it.
		glReadPixels(0, 0, getWidth(), getHeight(), GL_RGBA, GL_UNSIGNED_BYTE, buffer);

		// Transfers the data from the buffer into the image. This requires bit shifts to get the components data.
		for (int x = destination.getWidth() - 1; x >= 0; x--) {
			for (int y = destination.getHeight() - 1; y >= 0; y--) {
				int i = (x + getWidth() * y) * 4;
				destination.setRGB(x, destination.getHeight() - 1 - y, (((buffer.get(i) & 0xFF) & 0x0ff) << 16) | (((buffer.get(i + 1) & 0xFF) & 0x0ff) << 8) | ((buffer.get(i + 2) & 0xFF) & 0x0ff));
			}
		}

		return destination;
	}

	/**
	 * Gets the width of the display in pixels.
	 *
	 * @return The width of the display.
	 */
	public static int getWidth() {
		return INSTANCE.fullscreen ? INSTANCE.fullscreenWidth : INSTANCE.windowWidth;
	}

	public static int getWindowWidth() {
		return INSTANCE.windowWidth;
	}

	/**
	 * Gets the height of the display in pixels.
	 *
	 * @return The height of the display.
	 */
	public static int getHeight() {
		return INSTANCE.fullscreen ? INSTANCE.fullscreenHeight : INSTANCE.windowHeight;
	}

	public static int getWindowHeight() {
		return INSTANCE.windowHeight;
	}

	/**
	 * Gets the aspect ratio between the displays width and height.
	 *
	 * @return The aspect ratio.
	 */
	public static float getAspectRatio() {
		return ((float) getWidth()) / ((float) getHeight());
	}

	/**
	 * Gets the window's title.
	 *
	 * @return The window's title.
	 */
	public static String getTitle() {
		return INSTANCE.title;
	}

	/**
	 * gets if the display is using vSync.
	 *
	 * @return If VSync is enabled.
	 */
	public static boolean isVSync() {
		return INSTANCE.vsync;
	}

	/**
	 * Sets the display to use VSync or not.
	 *
	 * @param vsync Weather or not to use vSync.
	 */
	public static void setVSync(boolean vsync) {
		INSTANCE.vsync = vsync;
		glfwSwapInterval(vsync ? 1 : 0);

		if (vsync) {
			Framework.setFpsLimit(-1);
		}
	}

	/**
	 * Gets if the display requests antialiased images.
	 *
	 * @return If using antialiased images.
	 */
	public static boolean isAntialiasing() {
		return INSTANCE.antialiasing;
	}

	/**
	 * Requests the display to antialias.
	 *
	 * @param antialiasing If the display should antialias.
	 */
	public static void setAntialiasing(boolean antialiasing) {
		INSTANCE.antialiasing = antialiasing;
	}

	/**
	 * Gets how many MFAA samples should be done before swapping buffers.
	 *
	 * @return Amount of MFAA samples.
	 */
	public static int getSamples() {
		return INSTANCE.samples;
	}

	/**
	 * Gets how many MFAA samples should be done before swapping buffers. Zero disables multisampling. GLFW_DONT_CARE means no preference.
	 *
	 * @param samples The amount of MFSS samples.
	 */
	public static void setSamples(int samples) {
		INSTANCE.samples = samples;
		glfwWindowHint(GLFW_SAMPLES, samples);
	}

	/**
	 * Gets weather the display is fullscreen or not.
	 *
	 * @return Fullscreen or windowed.
	 */
	public static boolean isFullscreen() {
		return INSTANCE.fullscreen;
	}

	/**
	 * Sets the display to be fullscreen or windowed.
	 *
	 * @param fullscreen Weather or not to be fullscreen.
	 */
	public static void setFullscreen(boolean fullscreen) {
		if (INSTANCE.fullscreen == fullscreen) {
			return;
		}

		INSTANCE.fullscreen = fullscreen;
		long monitor = glfwGetPrimaryMonitor();
		GLFWVidMode mode = glfwGetVideoMode(monitor);

		FlounderLogger.log(fullscreen ? "Display is going fullscreen." : "Display is going windowed.");

		if (fullscreen) {
			INSTANCE.fullscreenWidth = mode.width();
			INSTANCE.fullscreenHeight = mode.height();
			glfwSetWindowMonitor(INSTANCE.window, monitor, 0, 0, INSTANCE.fullscreenWidth, INSTANCE.fullscreenHeight, GLFW_DONT_CARE);
		} else {
			INSTANCE.windowPosX = (mode.width() - INSTANCE.windowWidth) / 2;
			INSTANCE.windowPosY = (mode.height() - INSTANCE.windowHeight) / 2;
			glfwSetWindowMonitor(INSTANCE.window, NULL, INSTANCE.windowPosX, INSTANCE.windowPosY, INSTANCE.windowWidth, INSTANCE.windowHeight, GLFW_DONT_CARE);
		}
	}

	/**
	 * Gets the current GLFW window.
	 *
	 * @return The current GLFW window.
	 */
	public static long getWindow() {
		return INSTANCE.window;
	}

	/**
	 * Gets if the GLFW display is closed.
	 *
	 * @return If the GLFW display is closed.
	 */
	public static boolean isClosed() {
		return INSTANCE.closed;
	}

	/**
	 * Gets if the GLFW display is selected.
	 *
	 * @return If the GLFW display is selected.
	 */
	public static boolean isFocused() {
		return INSTANCE.focused;
	}

	/**
	 * Gets the windows Y position of the display in pixels.
	 *
	 * @return The windows x position.
	 */
	public static int getWindowXPos() {
		return INSTANCE.windowPosX;
	}

	/**
	 * Gets the windows Y position of the display in pixels.
	 *
	 * @return The windows Y position.
	 */
	public static int getWindowYPos() {
		return INSTANCE.windowPosY;
	}

	/**
	 * @return The current GLFW time time in seconds.
	 */
	public static float getTime() {
		return (float) (glfwGetTime() * 1000.0f);
	}

	@Override
	public Module getInstance() {
		return INSTANCE;
	}

	@Override
	public void dispose() {
		callbackWindowClose.free();
		callbackWindowFocus.free();
		callbackWindowPos.free();
		callbackWindowSize.free();
		callbackFramebufferSize.free();

		//	destroy(); // Would normally unload LWJGL natives, but for the Founder Engine we want to be able to reload the Engine in runtime.
		glfwDestroyWindow(window);
		glfwTerminate();

		INSTANCE.closed = false;
		INSTANCE.setup = false;
	}
}
