package flounder.devices;

import flounder.engine.*;

import java.io.*;
import java.nio.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * Manages the creation, updating and destruction of joysticks.
 */
public class DeviceJoysticks {
	private final Joystick joysticks[];

	/**
	 * Creates a new GLFW joystick manager.
	 */
	protected DeviceJoysticks() {
		joysticks = new Joystick[GLFW_JOYSTICK_LAST];
		update(0.0f);
	}

	/**
	 * Updates all connected joysticks, and finds new ones. Should be called once every frame.
	 *
	 * @param delta The time in seconds since the last frame.
	 */
	protected void update(final float delta) {
		// Gets connected Joysticks.
		for (int j = GLFW_JOYSTICK_1; j < GLFW_JOYSTICK_LAST; j++) {
			if (glfwJoystickPresent(j) == GL_FALSE) {
				if (joysticks[j] != null) {
					FlounderLogger.log("Disconnecting Joystick: " + j);
					joysticks[j] = null;
				}
			} else if (joysticks[j] == null) {
				FlounderLogger.log("Connecting Joystick: " + j);
				joysticks[j] = new Joystick(j);
			}
		}

		// Updates all connected Joysticks.
		for (Joystick k : joysticks) {
			if (k != null) {
				k.update();
			}
		}
	}

	/**
	 * Dobermans if the GLFW joystick is connected
	 *
	 * @param joystick The joystick to check connection with.
	 *
	 * @return If the joystick is connected.
	 */
	public boolean isConnected(final int joystick) {
		return joystick >= 0 && joystick < GLFW_JOYSTICK_LAST && joysticks[joystick] != null;
	}

	/**
	 * Gets the joystick from a GLFW joystick id.
	 *
	 * @param joystick The GLFW id to use in the search.
	 *
	 * @return The GLFW joystick object.
	 */
	public Joystick getJoystick(final int joystick) {
		return joysticks[joystick];
	}

	/**
	 * Closes the GLFW joystick system, do not use joysticks after calling this.
	 */
	protected void dispose() {
	}

	/**
	 * Represents a connected GLFW joystick.
	 */
	public class Joystick {
		private final int joystickID;
		private final String name;
		private float axes[];
		private byte buttons[];

		/**
		 * Creates a new GLFW joystick object.
		 *
		 * @param joystickID the GLFW joystick ID.
		 */
		public Joystick(final int joystickID) {
			this.joystickID = joystickID;
			name = glfwGetJoystickName(joystickID);
			update();
		}

		/**
		 * Updates the joysticks readings.
		 */
		protected void update() {
			// TODO: Fix all of the objects being created by GLFW!
			FloatBuffer fbaxes = glfwGetJoystickAxes(joystickID);
			ByteBuffer bbbuttons = glfwGetJoystickButtons(joystickID);

			if (fbaxes != null) {
				if (axes == null || axes.length != fbaxes.capacity()) {
					axes = new float[fbaxes.capacity()];
				}

				for (int i = 0; i < fbaxes.capacity(); i++) {
					axes[i] = fbaxes.get(i);
				}

				fbaxes.clear();
			}

			if (bbbuttons != null) {
				if (buttons == null || buttons.length != bbbuttons.capacity()) {
					buttons = new byte[bbbuttons.capacity()];
				}

				for (int i = 0; i < bbbuttons.capacity(); i++) {
					buttons[i] = bbbuttons.get(i);
				}

				bbbuttons.clear();
			}

			// writeData()
		}

		/**
		 * Writes the joysticks data into a file.
		 */
		public void writeData() {
			try {
				PrintStream ps = new PrintStream("joystick" + joystickID + ".txt");
				ps.println("[" + joystickID + "]: " + name);
				ps.println("\nAXES");

				for (int i = 0; i < axes.length; i++) {
					ps.println("[" + i + "]: " + axes[i]);
				}

				ps.println("\nBUTTONS");

				for (int i = 0; i < buttons.length; i++) {
					ps.println("[" + i + "]: " + buttons[i]);
				}

				ps.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		/**
		 * Gets the GLFW joystick ID.
		 *
		 * @return The GLFW joystick ID.
		 */
		public int getJoystickID() {
			return joystickID;
		}

		/**
		 * Gets the name of the joystick.
		 *
		 * @return The joysticks name.
		 */
		public String getName() {
			return name;
		}

		/**
		 * Gets the value of a joystick's axis.
		 *
		 * @param axis The axis of interest.
		 *
		 * @return The value of the joystick's axis.
		 */
		public float getAxis(final int axis) {
			return axes[axis];
		}

		/**
		 * Gets the whether a button on a joystick is pressed.
		 *
		 * @param button The button of interest.
		 *
		 * @return Whether a button on a joystick is pressed.
		 */
		public boolean getButton(final int button) {
			return buttons[button] != 0;
		}

		/**
		 * Gets the number of axes the joystick offers.
		 *
		 * @return The number of axes the joystick offers.
		 */
		public int getCountAxes() {
			return axes.length;
		}

		/**
		 * Gets the number of buttons the joystick offers.
		 *
		 * @return The number of buttons the joystick offers.
		 */
		public int getCountButtons() {
			return buttons.length;
		}
	}
}
