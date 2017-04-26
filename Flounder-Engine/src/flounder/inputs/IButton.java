package flounder.inputs;

/**
 * Interface for a binary input device.
 */
public interface IButton {
	/**
	 * Returns whether this button is currently pressed.
	 *
	 * @return True if the button is pressed, false otherwise.
	 */
	boolean isDown();

	/**
	 * Gets if the key is down and was not down before. Key press recognized as one click.
	 *
	 * @return Is the key down and was not down before?
	 */
	boolean wasDown();
}
