package flounder.inputs;

/**
 * Handles multiple buttons at once.
 */
public class CompoundButton implements IButton {
	private final IButton[] buttons;
	private boolean wasDown;

	/**
	 * Creates a new CompoundButton.
	 *
	 * @param buttons The list of buttons being checked.
	 */
	public CompoundButton(final IButton... buttons) {
		this.buttons = buttons;
		wasDown = false;
	}

	@Override
	public boolean isDown() {
		for (IButton button : buttons) {
			if (button.isDown()) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean wasDown() {
		boolean stillDown = wasDown && isDown();
		wasDown = isDown();
		return wasDown == !stillDown;
	}
}
