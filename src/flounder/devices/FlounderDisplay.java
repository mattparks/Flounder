package flounder.devices;

import flounder.fbos.*;
import flounder.framework.*;
import flounder.helpers.*;
import flounder.logger.*;
import flounder.platform.*;
import flounder.profiling.*;
import flounder.resources.*;
import org.lwjgl.glfw.*;

import javax.imageio.*;
import java.awt.image.*;
import java.io.*;
import java.nio.*;
import java.util.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.*;
import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * A module used for the creation, updating and destruction of the display.
 */
public class FlounderDisplay extends Module {
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
		super(FlounderDisplaySync.class);
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
	public void setup(int width, int height, String title, MyFile[] icons, boolean vsync, boolean antialiasing, int samples, boolean fullscreen, boolean hiddenDisplay) {
		//if (!this.isInitialized()) {
			this.windowWidth = width;
			this.windowHeight = height;
			this.title = title;
			this.icons = icons;
			this.vsync = vsync;
			this.antialiasing = antialiasing;
			this.samples = samples;
			this.fullscreen = fullscreen;
			this.hiddenDisplay = hiddenDisplay;
			this.setup = true;
	//	} else {
		//	FlounderLogger.get().error("Flounder Display setup MUST be called before the this is initialized.");
		//}
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
		if (!setup) {
			FlounderLogger.get().error("Flounder Display setup can be used to configure the display, default settings were set!");
			//	this.windowWidth = 1080;
			//	this.windowHeight = 720;
			//	this.title = "Testing 1";
			//	this.icons = new MyFile[]{new MyFile(MyFile.RES_FOLDER, "flounder.png")};
			this.vsync = true;
			this.antialiasing = true;
			this.samples = 0;
			this.fullscreen = false;
			this.setup = true;
		}

		// Fix any invalid parameters.
		if (windowWidth <= 0) {
			windowWidth = 1080;
		}

		if (windowHeight <= 0) {
			windowHeight = 720;
		}

		if (title == null || title.isEmpty()) {
			this.title = "Testing 1";
		}

		if (icons == null) {
			this.icons = new MyFile[]{new MyFile(MyFile.RES_FOLDER, "flounder.png")};
		}

		// Initialize the GLFW library.
		if (!glfwInit()) {
			FlounderLogger.get().error("Could not init GLFW!");
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
			FlounderLogger.get().error("Could not create the window! Update your graphics drivers and ensure your PC supports OpenGL 3.0!");
			glfwTerminate();
			System.exit(-1);
		}

		// Creates the OpenGL context.
		glfwMakeContextCurrent(window);

		try {
			setWindowIcon();
		} catch (IOException e) {
			FlounderLogger.get().error("Could not load custom display icon!");
			FlounderLogger.get().exception(e);
		}

		// LWJGL will detect the context that is current in the current thread, creates the GLCapabilities instance and makes the OpenGL bindings available for use.
		createCapabilities(true);

		// Gets any OpenGL errors.
		long glError = glGetError();

		if (glError != GL_NO_ERROR) {
			FlounderLogger.get().error("OpenGL Capability Error: " + glError);
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
		FlounderLogger.get().log("");
		FlounderLogger.get().log("===== This is not an error message, it is a system info log. =====");
		FlounderLogger.get().log("Flounder Engine Version: " + Framework.getVersion().getVersion());
		FlounderLogger.get().log("Flounder Operating System: " + System.getProperty("os.name"));
		FlounderLogger.get().log("Flounder OpenGL Version: " + glGetString(GL_VERSION));
		FlounderLogger.get().log("Flounder Is OpenGL Modern: " + OpenGlUtils.isModern());
		FlounderLogger.get().log("Flounder OpenGL Vendor: " + glGetString(GL_VENDOR));
		FlounderLogger.get().log("Flounder Available Processors (cores): " + Runtime.getRuntime().availableProcessors());
		FlounderLogger.get().log("Flounder Free Memory (bytes): " + Runtime.getRuntime().freeMemory());
		FlounderLogger.get().log("Flounder Maximum Memory (bytes): " + (Runtime.getRuntime().maxMemory() == Long.MAX_VALUE ? "Unlimited" : Runtime.getRuntime().maxMemory()));
		FlounderLogger.get().log("Flounder Total Memory Available To JVM (bytes): " + Runtime.getRuntime().totalMemory());
		FlounderLogger.get().log("Flounder Maximum FBO Size: " + FBO.getMaxFBOSize());
		FlounderLogger.get().log("Flounder Maximum Anisotropy: " + glGetFloat(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
		FlounderLogger.get().log("===== End of system info log. =====\n");
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
		ByteBuffer buffer = FlounderPlatform.get().createByteBuffer(width * height * 4);

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

	@Handler.Function(Handler.FLAG_UPDATE_ALWAYS)
	public void update() {
		// Polls for window events. The key callback will only be invoked during this call.
		glfwPollEvents();
	}

	/**
	 * Updates the display image by swapping the colour buffers.
	 */
	public void swapBuffers() {
		if (this.window != 0) {
			glfwSwapBuffers(this.window);
		}
	}

	@Handler.Function(Handler.FLAG_PROFILE)
	public void profile() {
		FlounderProfiler.get().add(getTab(), "Width", windowWidth);
		FlounderProfiler.get().add(getTab(), "Height", windowHeight);
		FlounderProfiler.get().add(getTab(), "Title", title);
		FlounderProfiler.get().add(getTab(), "VSync", vsync);
		FlounderProfiler.get().add(getTab(), "Antialiasing", antialiasing);
		FlounderProfiler.get().add(getTab(), "Samples", samples);
		FlounderProfiler.get().add(getTab(), "Fullscreen", fullscreen);

		FlounderProfiler.get().add(getTab(), "Closed", closed);
		FlounderProfiler.get().add(getTab(), "Focused", focused);
		FlounderProfiler.get().add(getTab(), "Window Pos.X", windowPosX);
		FlounderProfiler.get().add(getTab(), "Window Pos.Y", windowPosY);
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
		// Creates a new destination if it does not exist, or fixes a old one,
		if (destination == null || buffer == null || destination.getWidth() != getWidth() || destination.getHeight() != getHeight()) {
			destination = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
			buffer = FlounderPlatform.get().createByteBuffer(getWidth() * getHeight() * 4);
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
	public int getWidth() {
		return this.fullscreen ? this.fullscreenWidth : this.windowWidth;
	}

	public int getWindowWidth() {
		return this.windowWidth;
	}

	/**
	 * Gets the height of the display in pixels.
	 *
	 * @return The height of the display.
	 */
	public int getHeight() {
		return this.fullscreen ? this.fullscreenHeight : this.windowHeight;
	}

	public int getWindowHeight() {
		return this.windowHeight;
	}

	public void setWindowSize(int width, int height) {
		glfwSetWindowSize(this.window, width, height);
		this.windowWidth = width;
		this.windowHeight = height;
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
		return this.title;
	}

	/**
	 * gets if the display is using vSync.
	 *
	 * @return If VSync is enabled.
	 */
	public boolean isVSync() {
		return this.vsync;
	}

	/**
	 * Sets the display to use VSync or not.
	 *
	 * @param vsync Weather or not to use vSync.
	 */
	public void setVSync(boolean vsync) {
		this.vsync = vsync;
		glfwSwapInterval(vsync ? 1 : 0);

		if (vsync) {
			Framework.setFpsLimit(60);
		}
	}

	/**
	 * Gets if the display requests antialiased images.
	 *
	 * @return If using antialiased images.
	 */
	public boolean isAntialiasing() {
		return this.antialiasing;
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
		return this.samples;
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
		return this.fullscreen;
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
		long monitor = glfwGetPrimaryMonitor();
		GLFWVidMode mode = glfwGetVideoMode(monitor);

		FlounderLogger.get().log(fullscreen ? "Display is going fullscreen." : "Display is going windowed.");

		if (fullscreen) {
			this.fullscreenWidth = mode.width();
			this.fullscreenHeight = mode.height();
			glfwSetWindowMonitor(this.window, monitor, 0, 0, this.fullscreenWidth, this.fullscreenHeight, GLFW_DONT_CARE);
		} else {
			this.windowPosX = (mode.width() - this.windowWidth) / 2;
			this.windowPosY = (mode.height() - this.windowHeight) / 2;
			glfwSetWindowMonitor(this.window, NULL, this.windowPosX, this.windowPosY, this.windowWidth, this.windowHeight, GLFW_DONT_CARE);
		}
	}

	/**
	 * Gets the current GLFW window.
	 *
	 * @return The current GLFW window.
	 */
	public long getWindow() {
		return this.window;
	}

	/**
	 * Gets if the GLFW display is closed.
	 *
	 * @return If the GLFW display is closed.
	 */
	public boolean isClosed() {
		return this.closed;
	}

	/**
	 * Gets if the GLFW display is selected.
	 *
	 * @return If the GLFW display is selected.
	 */
	public boolean isFocused() {
		return this.focused;
	}

	/**
	 * Gets the windows Y position of the display in pixels.
	 *
	 * @return The windows x position.
	 */
	public int getWindowXPos() {
		return this.windowPosX;
	}

	/**
	 * Gets the windows Y position of the display in pixels.
	 *
	 * @return The windows Y position.
	 */
	public int getWindowYPos() {
		return this.windowPosY;
	}

	/**
	 * @return The current GLFW time time in seconds.
	 */
	public float getTime() {
		return (float) (glfwGetTime() * 1000.0f);
	}


	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
		callbackWindowClose.free();
		callbackWindowFocus.free();
		callbackWindowPos.free();
		callbackWindowSize.free();
		callbackFramebufferSize.free();

		//	destroy(); // Would normally unload LWJGL natives, but for the Founder Engine we want to be able to reload the Engine in runtime.
		glfwDestroyWindow(window);
		glfwTerminate();

		this.closed = false;
		this.setup = false;
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
