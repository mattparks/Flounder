package flounder.inputs;

/**
 * Handles multiple buttons at once.
 */
public class CompoundButton implements IButton {
	private final IButton[] m_buttons;
	private boolean m_wasDown;

	/**
	 * Creates a new CompoundButton.
	 *
	 * @param buttons The list of buttons being checked.
	 */
	public CompoundButton(final IButton... buttons) {
		m_buttons = buttons;
		m_wasDown = false;
	}

	@Override
	public boolean isDown() {
		for (IButton button : m_buttons) {
			if (button.isDown()) {
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
}
