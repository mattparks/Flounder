package flounder.inputs;

/**
 * Base class for typical buttons.
 */
public abstract class BaseButton implements IButton {
	private final Command command;
	private final int[] codes;
	private boolean wasDown;

	/**
	 * Creates a new BaseButton.
	 *
	 * @param command The method of deciding if a code is down or not in the input system.
	 * @param codes The list of codes this button is checking.
	 */
	public BaseButton(final Command command, final int... codes) {
		this.command = command;
		this.codes = codes;
		wasDown = false;
	}

	@Override
	public boolean isDown() {
		if (codes == null) {
			return false;
		}

		for (int code : codes) {
			if (command.isDown(code)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean wasDown() {
		final boolean stillDown = wasDown && isDown();
		wasDown = isDown();
		return wasDown == !stillDown;
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
