package flounder.lwjgl3.devices;

import flounder.devices.*;
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

@Module.ModuleOverride
public class LwjglMouse extends FlounderMouse {
	private MyFile customMouse;

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

	public LwjglMouse() {
		this(new MyFile(MyFile.RES_FOLDER, "guis", "cursor.png"));
	}

	public LwjglMouse(MyFile customMouse) {
		super();
		this.customMouse = customMouse;
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
		this.mouseButtons = new int[GLFW_MOUSE_BUTTON_LAST];
		this.displaySelected = true;
		this.mousePositionX = 0.5f;
		this.mousePositionY = 0.5f;

		this.cursorDisabled = false;
		this.lastCursorDisabled = false;

		// Sets the mouse callbacks.
		glfwSetScrollCallback(FlounderDisplay.get().getWindow(), (window, xoffset, yoffset) -> {
			mouseDeltaWheel = (float) yoffset;
		});

		glfwSetMouseButtonCallback(FlounderDisplay.get().getWindow(), (window, button, action, mods) -> {
			mouseButtons[button] = action;
		});

		glfwSetCursorPosCallback(FlounderDisplay.get().getWindow(), (window, xpos, ypos) -> {
			mousePositionX = (float) (xpos / FlounderDisplay.get().getWidth());
			mousePositionY = (float) (ypos / FlounderDisplay.get().getHeight());
		});

		glfwSetCursorEnterCallback(FlounderDisplay.get().getWindow(), (window, entered) -> {
			displaySelected = entered;
		});

		try {
			createCustomMouse();
		} catch (IOException e) {
			FlounderLogger.get().error("Could not load custom mouse!");
			FlounderLogger.get().exception(e);
		}

		super.init();
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
		super.update();

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

	@Override
	public void setCursorHidden(boolean disabled) {
		if (this.cursorDisabled != disabled) {
			glfwSetInputMode(FlounderDisplay.get().getWindow(), GLFW_CURSOR, (disabled ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL));

			if (!disabled && this.cursorDisabled) {
				glfwSetCursorPos(FlounderDisplay.get().getWindow(), this.mousePositionX * FlounderDisplay.get().getWidth(), this.mousePositionY * FlounderDisplay.get().getHeight());
			}
		}

		this.cursorDisabled = disabled;
	}

	@Override
	public boolean getMouse(int button) {
		return this.mouseButtons[button] != GLFW_RELEASE;
	}

	@Override
	public float getPositionX() {
		return this.mousePositionX;
	}

	@Override
	public float getPositionY() {
		return this.mousePositionY;
	}

	@Override
	public void setPosition(float cursorX, float cursorY) {
		glfwSetCursorPos(FlounderDisplay.get().getWindow(), cursorX, cursorY);
	}

	@Override
	public float getDeltaX() {
		return this.mouseDeltaX;
	}

	@Override
	public float getDeltaY() {
		return this.mouseDeltaY;
	}

	@Override
	public float getDeltaWheel() {
		return this.mouseDeltaWheel;
	}

	@Override
	public boolean isDisplaySelected() {
		return this.displaySelected;
	}

	@Override
	public boolean isCursorDisabled() {
		return this.cursorDisabled;
	}

	@Handler.Function(Handler.FLAG_PROFILE)
	public void profile() {
		super.profile();
		FlounderProfiler.get().add(getTab(), "Position X", mousePositionX);
		FlounderProfiler.get().add(getTab(), "Position Y", mousePositionY);
		FlounderProfiler.get().add(getTab(), "Delta X", mouseDeltaX);
		FlounderProfiler.get().add(getTab(), "Delta Y", mouseDeltaY);
		FlounderProfiler.get().add(getTab(), "Delta Wheel", mouseDeltaWheel);
		FlounderProfiler.get().add(getTab(), "Selected Display", displaySelected);
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
		super.dispose();
	}
}
