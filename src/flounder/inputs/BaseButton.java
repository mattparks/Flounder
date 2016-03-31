package flounder.inputs;

/**
 * Base class for typical buttons.
 */
public abstract class BaseButton implements IButton {
	private final Command m_command;
	private final int[] m_codes;
	private boolean m_wasDown;

	/**
	 * Creates a new BaseButton.
	 *
	 * @param command The method of deciding if a code is down or not in the input system.
	 * @param codes The list of codes this button is checking.
	 */
	public BaseButton(final Command command, final int... codes) {
		m_command = command;
		m_codes = codes;
		m_wasDown = false;
	}

	@Override
	public boolean isDown() {
		if (m_codes == null) {
			return false;
		}

		for (int code : m_codes) {
			if (m_command.isDown(code)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean wasDown() {
		boolean stillDown = m_wasDown && isDown();
		m_wasDown = isDown();
		return m_wasDown == !stillDown;
	}

	/**
	 * Decides whether a certain code is down or not.
	 */
	public interface Command {
		/**
		 * Decides whether a certain code is down or not.
		 *
		 * @param code The button code
		 *
		 * @return True if the button specified by the code is down in the input system, false otherwise.
		 */
		boolean isDown(final int code);
	}
}
