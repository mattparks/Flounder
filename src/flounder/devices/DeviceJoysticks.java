package flounder.devices;

import java.io.*;
import java.nio.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * Manages the creation, updating and destruction of joysticks.
 */
public class DeviceJoysticks {
	private final Joystick m_joysticks[];

	/**
	 * Creates a new GLFW m_joystick manager.
	 */
	protected DeviceJoysticks() {
		m_joysticks = new Joystick[GLFW_JOYSTICK_LAST];
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
				if (m_joysticks[j] != null) {
					System.out.println("Disconnecting Joystick: " + j);
					m_joysticks[j] = null;
				}
			} else if (m_joysticks[j] == null) {
				System.out.println("Connecting Joystick: " + j);
				m_joysticks[j] = new Joystick(j);
			}
		}

		// Updates all connected Joysticks.
		for (Joystick k : m_joysticks) {
			if (k != null) {
				k.update();
			}
		}
	}

	/**
	 * Dobermans if the GLFW m_joystick is connected
	 *
	 * @param joystick The joystick to check connection with.
	 *
	 * @return If the joystick is connected.
	 */
	public boolean isConnected(final int joystick) {
		return joystick >= 0 && joystick < GLFW_JOYSTICK_LAST && m_joysticks[joystick] != null;
	}

	/**
	 * Gets the joystick from a GLFW m_joystick id.
	 *
	 * @param joystick The GLFW id to use in the search.
	 *
	 * @return The GLFW joystick object.
	 */
	public Joystick getJoystick(final int joystick) {
		return m_joysticks[joystick];
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
		private final int m_joystick;
		private final String m_name;
		private float m_axes[];
		private byte m_buttons[];

		/**
		 * Creates a new GLFW joystick object.
		 *
		 * @param joystick the GLFW joystick number.
		 */
		public Joystick(final int joystick) {
			m_joystick = joystick;
			m_name = glfwGetJoystickName(joystick);
			update();
		}

		/**
		 * Updates the joysticks readings.
		 */
		protected void update() {
			// TODO: Fix all of the objects being created by GLFW!
			FloatBuffer fbaxes = glfwGetJoystickAxes(m_joystick);
			ByteBuffer bbbuttons = glfwGetJoystickButtons(m_joystick);

			if (fbaxes != null) {
				if (m_axes == null || m_axes.length != fbaxes.capacity()) {
					m_axes = new float[fbaxes.capacity()];
				}

				for (int i = 0; i < fbaxes.capacity(); i++) {
					m_axes[i] = fbaxes.get(i);
				}

				fbaxes.clear();
			}

			if (bbbuttons != null) {
				if (m_buttons == null || m_buttons.length != bbbuttons.capacity()) {
					m_buttons = new byte[bbbuttons.capacity()];
				}

				for (int i = 0; i < bbbuttons.capacity(); i++) {
					m_buttons[i] = bbbuttons.get(i);
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
				PrintStream ps = new PrintStream("m_joystick" + m_joystick + ".txt");
				ps.println("[" + m_joystick + "]: " + m_name);
				ps.println("\nAXES");
				for (int i = 0; i < m_axes.length; i++) {
					ps.println("[" + i + "]: " + m_axes[i]);
				}
				ps.println("\nBUTTONS");
				for (int i = 0; i < m_buttons.length; i++) {
					ps.println("[" + i + "]: " + m_buttons[i]);
				}
				ps.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		/**
		 * Gets the GLFW joystick number.
		 *
		 * @return The GLFW joystick number.
		 */
		public int getJoystick() {
			return m_joystick;
		}

		/**
		 * Gets the m_name of the joystick.
		 *
		 * @return The joysticks name.
		 */
		public String getName() {
			return m_name;
		}

		/**
		 * Gets the value of a joystick's axis.
		 *
		 * @param axis The axis of interest.
		 *
		 * @return The value of the joystick's axis.
		 */
		public float getAxis(final int axis) {
			return m_axes[axis];
		}

		/**
		 * Gets the whether a button on a joystick is pressed.
		 *
		 * @param button The button of interest.
		 *
		 * @return Whether a button on a joystick is pressed.
		 */
		public boolean getButton(final int button) {
			return m_buttons[button] != 0;
		}

		/**
		 * Gets the number of axes the joystick offers.
		 *
		 * @return The number of axes the joystick offers.
		 */
		public int getCountAxes() {
			return m_axes.length;
		}

		/**
		 * Gets the number of buttons the joystick offers.
		 *
		 * @return The number of buttons the joystick offers.
		 */
		public int getCountButtons() {
			return m_buttons.length;
		}
	}
}
