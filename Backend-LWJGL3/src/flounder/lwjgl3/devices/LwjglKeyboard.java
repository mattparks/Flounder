package flounder.lwjgl3.devices;

import flounder.devices.*;
import flounder.framework.*;
import flounder.logger.*;
import org.lwjgl.glfw.*;

import static org.lwjgl.glfw.GLFW.*;

@Module.ModuleOverride
public class LwjglKeyboard extends FlounderKeyboard {
	private int keyboardKeys[];
	private int keyboardChar;

	private GLFWKeyCallback callbackKey;
	private GLFWCharCallback callbackChar;

	public LwjglKeyboard() {
		super();
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
		this.keyboardKeys = new int[GLFW_KEY_LAST + 1];
		this.keyboardChar = 0;

		// Sets the keyboards callbacks.
		glfwSetKeyCallback(FlounderDisplay.get().getWindow(), callbackKey = new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				// TODO: Play with mods.

				if (key < 0 || key > GLFW_KEY_LAST) {
					FlounderLogger.get().error("Invalid action attempted with key " + key);
				} else {
					keyboardKeys[key] = action;
				}
			}
		});

		glfwSetCharCallback(FlounderDisplay.get().getWindow(), callbackChar = new GLFWCharCallback() {
			@Override
			public void invoke(long window, int codepoint) {
				keyboardChar = codepoint;
			}
		});

		super.init();
	}

	@Handler.Function(Handler.FLAG_UPDATE_PRE)
	public void update() {
		super.update();
	}

	@Override
	public boolean getKey(int key) {
		return this.keyboardKeys[key] != GLFW_RELEASE;
	}

	@Override
	public int getKeyboardChar() {
		return this.keyboardChar;
	}

	@Handler.Function(Handler.FLAG_PROFILE)
	public void profile() {
		super.profile();
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
		super.dispose();
		callbackKey.free();
		callbackChar.free();
	}
}
