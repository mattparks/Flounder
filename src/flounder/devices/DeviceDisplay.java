package flounder.devices;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * Manages the creation, updating and destruction of the display, as well as timing and frame times.
 */
public class DeviceDisplay {
	private final GLFWWindowCloseCallback m_callbackWindowClose;
	private final GLFWWindowFocusCallback m_callbackWindowFocus;
	private final GLFWWindowPosCallback m_callbackWindowPos;
	private final GLFWWindowSizeCallback m_callbackWindowSize;
	private final GLFWFramebufferSizeCallback m_callbackFramebufferSize;

	private long m_window;
	private int m_displayWidth;
	private int m_displayHeight;
	private String m_displayTitle;
	private boolean m_displayVSync;
	private boolean m_antialiasing;
	private boolean m_displayFullscreen;
	private int m_xpos, m_ypos;
	private boolean m_focused;
	private boolean m_closeRequested;

	/**
	 * Creates a new GLFW window.
	 *
	 * @param displayWidth The window width in pixels.
	 * @param displayHeight The window height in pixels.
	 * @param displayTitle The window title.
	 * @param displayVSync If the window will use vSync..
	 * @param antialiasing If OpenGL will use altialiasing.
	 * @param displayFullscreen If the window will start fullscreen.
	 */
	protected DeviceDisplay(final int displayWidth, final int displayHeight, final String displayTitle, final boolean displayVSync, final boolean antialiasing, final boolean displayFullscreen) {
		m_displayWidth = displayWidth;
		m_displayHeight = displayHeight;
		m_displayTitle = displayTitle;
		m_displayVSync = displayVSync;
		m_antialiasing = antialiasing;
		m_displayFullscreen = displayFullscreen;
		m_focused = true;
		m_closeRequested = false;

		// Initialize the library.
		if (glfwInit() != GLFW_TRUE) {
			System.out.println("Could not init GLFW!");
			System.exit(-1);
		}

		// Configures the window.
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // The window will stay hidden until after creation.
		glfwWindowHint(GLFW_RESIZABLE, displayFullscreen ? GL_FALSE : GL_TRUE); // The window will be resizable depending on if its createDisplay.
		// glfwWindowHint(GLFW_SAMPLES, 8);
		// glfwWindowHint(GLFW_REFRESH_RATE, 60);

		// Gets the resolution of the primary monitor.
		final GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

		// Create a windowed mode window and its OpenGL context.
		m_window = glfwCreateWindow(displayWidth, displayHeight, displayTitle, NULL, NULL);

		// Sets the display to fullscreen or windowed.
		setDisplayFullscreen(displayFullscreen);

		// Gets any window errors.
		if (m_window == NULL) {
			System.out.println("Could not create the window!");
			glfwTerminate();
			System.exit(-1);
		}

		// Creates the OpenGL context.
		glfwMakeContextCurrent(m_window);

		// LWJGL will detect the context that is current in the current thread, creates the GLCapabilities instance and makes the OpenGL bindings available for use.
		GL.createCapabilities();

		// Gets any OpenGL errors.
		final long glError = glGetError();

		if (glError != GL_NO_ERROR) {
			System.out.println("OpenGL Error: " + glError);
			glfwDestroyWindow(m_window);
			glfwTerminate();
			System.exit(-1);
		}

		// Enables VSync if requested.
		setDisplayVSync(displayVSync);

		// Centers the window position.
		glfwSetWindowPos(m_window, (m_xpos = (vidmode.width() - displayWidth) / 2), (m_ypos = (vidmode.height() - displayHeight) / 2));

		// Shows the OpenGl window.
		glfwShowWindow(m_window);

		// Sets the displays callbacks.
		glfwSetWindowCloseCallback(m_window, m_callbackWindowClose = new GLFWWindowCloseCallback() {
			@Override
			public void invoke(long window) {
				m_closeRequested = true;
			}
		});

		glfwSetWindowFocusCallback(m_window, m_callbackWindowFocus = new GLFWWindowFocusCallback() {
			@Override
			public void invoke(long window, int focused) {
				m_focused = focused == GL_TRUE;
			}
		});

		glfwSetWindowPosCallback(m_window, m_callbackWindowPos = new GLFWWindowPosCallback() {
			@Override
			public void invoke(long window, int xpos, int ypos) {
				m_xpos = xpos;
				m_ypos = ypos;
			}
		});

		glfwSetWindowSizeCallback(m_window, m_callbackWindowSize = new GLFWWindowSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				m_displayWidth = width;
				m_displayHeight = height;
			}
		});

		glfwSetFramebufferSizeCallback(m_window, m_callbackFramebufferSize = new GLFWFramebufferSizeCallback() {
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
		glfwSwapBuffers(m_window);
	}

	/**
	 * @return The current GLFW window.
	 */
	public long getWindow() {
		return m_window;
	}

	/**
	 * @return The width of the display in pixels.
	 */
	public int getDisplayWidth() {
		return m_displayWidth;
	}

	/**
	 * @return The height of the display in pixels.
	 */
	public int getDisplayHeight() {
		return m_displayHeight;
	}

	/**
	 * @return The aspect ratio between the displays width and height.
	 */
	public float getDisplayAspectRatio() {
		return ((float) m_displayWidth) / ((float) m_displayHeight);
	}

	/**
	 * @return The window's title.
	 */
	public String getDisplayTitle() {
		return m_displayTitle;
	}

	/**
	 * @return If the display is using vSync.
	 */
	public boolean isDisplayVSync() {
		return m_displayVSync;
	}

	/**
	 * Set the display to use VSync or not.
	 *
	 * @param displayVSync Weather or not to use vSync.
	 */
	public void setDisplayVSync(final boolean displayVSync) {
		m_displayVSync = displayVSync;
		glfwSwapInterval(m_displayVSync ? 1 : 0);
	}

	/**
	 * @return If the display requests antialiased images.
	 */
	public boolean isAntialiasing() {
		return m_antialiasing;
	}

	/**
	 * Requests the display to antialias.
	 *
	 * @param antialiasing If the display should antialias.
	 */
	public void setAntialiasing(final boolean antialiasing) {
		m_antialiasing = antialiasing;
	}

	/**
	 * @return Weather the display is fullscreen or not.
	 */
	public boolean isDisplayFullscreen() {
		return m_displayFullscreen;
	}

	/**
	 * Set the display to fullscreen or windowed.
	 *
	 * @param displayFullscreen Weather or not to be fullscreen.
	 */
	public void setDisplayFullscreen(final boolean displayFullscreen) {
		m_displayFullscreen = displayFullscreen;
		// TODO: Put display in fullscreen!
		glfwWindowHint(GLFW_RESIZABLE, m_displayFullscreen ? GL_FALSE : GL_TRUE);
	}

	/**
	 * @return The x pos of the display in pixels.
	 */
	public int getXPos() {
		return m_xpos;
	}

	/**
	 * @return The y pos of the display in pixels.
	 */
	public int getYPos() {
		return m_ypos;
	}

	/**
	 * @return If the GLFW display is selected.
	 */
	public boolean isFocused() {
		return m_focused;
	}

	/**
	 * @return If the GLFW display is open or if close has not been requested.
	 */
	public boolean isOpen() {
		return !m_closeRequested && glfwWindowShouldClose(m_window) != GL_TRUE;
	}

	/**
	 * Indicates that the game has been requested to close. At the end of the current frame the main game loop will exit.
	 */
	public void requestClose() {
		m_closeRequested = true;
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
		m_callbackWindowClose.release();
		m_callbackWindowFocus.release();
		m_callbackWindowPos.release();
		m_callbackWindowSize.release();
		m_callbackFramebufferSize.release();
		glfwDestroyWindow(m_window);
		glfwTerminate();
	}
}
