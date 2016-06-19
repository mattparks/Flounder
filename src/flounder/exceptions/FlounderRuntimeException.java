package flounder.exceptions;

/**
 * A exception that can be throws when the runtime encounters a exception.
 */
public class FlounderRuntimeException extends RuntimeException {
	/**
	 * Exception with just a message.
	 *
	 * @param message The message.
	 */
	public FlounderRuntimeException(String message) {
		super(message);
	}

	/**
	 * Exception width a throwable.
	 *
	 * @param t The throwable.
	 */
	public FlounderRuntimeException(Throwable t) {
		super(t);
	}

	/**
	 * Exception with a message and a throwable.
	 *
	 * @param message The message.
	 * @param t The throwable.
	 */
	public FlounderRuntimeException(String message, Throwable t) {
		super(message, t);
	}
}
