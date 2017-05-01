package flounder.devices;

import flounder.framework.*;
import flounder.logger.*;
import flounder.maths.*;
import flounder.platform.*;
import flounder.profiling.*;
import flounder.resources.*;
import org.lwjgl.glfw.*;

import javax.imageio.*;
import java.awt.image.*;
import java.io.*;
import java.nio.*;

import static org.lwjgl.glfw.GLFW.*;

/**
 * A module used for the creation, updating and destruction of the mouse.
 */
public class FlounderMouse extends Module {
	private static MyFile customMouse;

	private int mouseButtons[];
	private float lastMousePositionX;
	private float lastMousePositionY;
	private float mousePositionX;
	private float mousePositionY;
	private float mouseDeltaX;
	private float mouseDeltaY;
	private float mouseDeltaWheel;
	private boolean displaySelected;

	private boolean cursorDisabled;
	private boolean lastCursorDisabled;

	private GLFWScrollCallback callbackScroll;
	private GLFWMouseButtonCallback callbackMouseButton;
	private GLFWCursorPosCallback callbackCursorPos;
	private GLFWCursorEnterCallback callbackCursorEnter;

	/**
	 * Creates a new GLFW mouse manager.
	 */
	public FlounderMouse() {
		super(FlounderLogger.class, FlounderDisplay.class);
	}

	/**
	 * A function called before initialization to configure the mouse.
	 *
	 * @param customMouse A png file containing a custom mouse cursor.
	 */
	public void setup(MyFile customMouse) {
		if (FlounderMouse.customMouse == customMouse) {
			return;
		}

		FlounderMouse.customMouse = customMouse;

	//	if (this.isInitialized()) {
			try {
				this.createCustomMouse();
			} catch (IOException e) {
				FlounderLogger.get().error("Could not load custom mouse!");
				FlounderLogger.get().exception(e);
			}
	//	}
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
		if (FlounderMouse.customMouse == null) {
			FlounderMouse.customMouse = new MyFile(MyFile.RES_FOLDER, "guis", "cursor.png");
		}

		this.mouseButtons = new int[GLFW_MOUSE_BUTTON_LAST];
		this.displaySelected = true;
		this.mousePositionX = 0.5f;
		this.mousePositionY = 0.5f;

		this.cursorDisabled = false;
		this.lastCursorDisabled = false;

		// Sets the mouse callbacks.
		glfwSetScrollCallback(FlounderDisplay.get().getWindow(), callbackScroll = new GLFWScrollCallback() {
			@Override
			public void invoke(long window, double xoffset, double yoffset) {
				mouseDeltaWheel = (float) yoffset;
			}
		});

		glfwSetMouseButtonCallback(FlounderDisplay.get().getWindow(), callbackMouseButton = new GLFWMouseButtonCallback() {
			@Override
			public void invoke(long window, int button, int action, int mods) {
				mouseButtons[button] = action;
			}
		});

		glfwSetCursorPosCallback(FlounderDisplay.get().getWindow(), callbackCursorPos = new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double xpos, double ypos) {
				mousePositionX = (float) (xpos / FlounderDisplay.get().getWidth());
				mousePositionY = (float) (ypos / FlounderDisplay.get().getHeight());
			}
		});

		glfwSetCursorEnterCallback(FlounderDisplay.get().getWindow(), callbackCursorEnter = new GLFWCursorEnterCallback() {
			@Override
			public void invoke(long window, boolean entered) {
				displaySelected = entered;
			}
		});

		try {
			createCustomMouse();
		} catch (IOException e) {
			FlounderLogger.get().error("Could not load custom mouse!");
			FlounderLogger.get().exception(e);
		}
	}

	private void createCustomMouse() throws IOException {
		BufferedImage image = ImageIO.read(customMouse.getInputStream());
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
		GLFWImage cursorImg = GLFWImage.create();
		cursorImg.width(width); // Setup the images' width.
		cursorImg.height(height); // Setup the images' height.
		cursorImg.pixels(buffer); // Pass image data.

		// Create custom cursor and store its ID.
		int hotspotX = 0;
		int hotspotY = 0;
		long cursorID = GLFW.glfwCreateCursor(cursorImg, hotspotX, hotspotY);

		// Set current cursor.
		glfwSetCursor(FlounderDisplay.get().getWindow(), cursorID);
	}

	@Handler.Function(Handler.FLAG_UPDATE_PRE)
	public void update() {
		// Updates the mouses delta.
		mouseDeltaX = Framework.getDelta() * (lastMousePositionX - mousePositionX);
		mouseDeltaY = Framework.getDelta() * (lastMousePositionY - mousePositionY);

		// Sets the last position of the current.
		lastMousePositionX = mousePositionX;
		lastMousePositionY = mousePositionY;

		// Fixes snaps when toggling cursor.
		if (cursorDisabled != lastCursorDisabled) {
			mouseDeltaX = 0.0f;
			mouseDeltaY = 0.0f;

			lastCursorDisabled = cursorDisabled;
		}

		// Updates the mouse wheel using a smooth scroll technique.
		if (mouseDeltaWheel != 0.0f) {
			mouseDeltaWheel -= Framework.getDelta() * ((mouseDeltaWheel < 0.0f) ? -1.0f : 1.0f);
			mouseDeltaWheel = Maths.deadband(0.1f, mouseDeltaWheel);
		}
	}

	@Handler.Function(Handler.FLAG_PROFILE)
	public void profile() {
		FlounderProfiler.get().add(getTab(), "Position X", mousePositionX);
		FlounderProfiler.get().add(getTab(), "Position Y", mousePositionY);
		FlounderProfiler.get().add(getTab(), "Delta X", mouseDeltaX);
		FlounderProfiler.get().add(getTab(), "Delta Y", mouseDeltaY);
		FlounderProfiler.get().add(getTab(), "Delta Wheel", mouseDeltaWheel);
		FlounderProfiler.get().add(getTab(), "Selected Display", displaySelected);
	}

	/**
	 * Sets if the operating systems cursor is hidden whilst in the display.
	 *
	 * @param disabled If the system cursor should be disabled or hidden when not shown.
	 */
	public void setCursorHidden(boolean disabled) {
		if (this.cursorDisabled != disabled) {
			glfwSetInputMode(FlounderDisplay.get().getWindow(), GLFW_CURSOR, (disabled ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL));

			if (!disabled && this.cursorDisabled) {
				glfwSetCursorPos(FlounderDisplay.get().getWindow(), this.mousePositionX * FlounderDisplay.get().getWidth(), this.mousePositionY * FlounderDisplay.get().getHeight());
			}
		}

		this.cursorDisabled = disabled;
	}

	/**
	 * Gets whether or not a particular mouse button is currently pressed.
	 * <p>GLFW Actions: GLFW_PRESS, GLFW_RELEASE, GLFW_REPEAT</p>
	 *
	 * @param button The mouse button to test.
	 *
	 * @return If the mouse button is currently pressed.
	 */
	public boolean getMouse(int button) {
		return this.mouseButtons[button] != GLFW_RELEASE;
	}

	/**
	 * Gets the mouses screen x position.
	 *
	 * @return The mouses x position.
	 */
	public float getPositionX() {
		return this.mousePositionX;
	}

	/**
	 * Gets the mouses screen y position.
	 *
	 * @return The mouses y position.
	 */
	public float getPositionY() {
		return this.mousePositionY;
	}

	public void setPosition(float cursorX, float cursorY) {
		glfwSetCursorPos(FlounderDisplay.get().getWindow(), cursorX, cursorY);
	}

	/**
	 * Gets the mouses delta x.
	 *
	 * @return The mouses delta x.
	 */
	public float getDeltaX() {
		return this.mouseDeltaX;
	}

	/**
	 * Gets the mouses delta y.
	 *
	 * @return The mouses delta y.
	 */
	public float getDeltaY() {
		return this.mouseDeltaY;
	}

	/**
	 * Gets the mouses wheel delta.
	 *
	 * @return The mouses wheel delta.
	 */
	public float getDeltaWheel() {
		return this.mouseDeltaWheel;
	}

	/**
	 * Gets if the display is selected.
	 *
	 * @return If the display is selected.
	 */
	public boolean isDisplaySelected() {
		return this.displaySelected;
	}

	/**
	 * If the cursor is hidden, the mouse is the display locked if true.
	 *
	 * @return If the cursor is hidden.
	 */
	public boolean isCursorDisabled() {
		return this.cursorDisabled;
	}


	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
		callbackScroll.free();
		callbackMouseButton.free();
		callbackCursorPos.free();
		callbackCursorEnter.free();
	}

	@Module.Instance
	public static FlounderMouse get() {
		return (FlounderMouse) Framework.getInstance(FlounderMouse.class);
	}

	@Module.TabName
	public static String getTab() {
		return "Mouse";
	}
}
