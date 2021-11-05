package flounder.framework;

import flounder.logger.*;

import java.lang.annotation.*;
import java.lang.reflect.*;

/**
 * A handler for modules used for modular function calls.
 */
public class Handler {
	public static final int FLAG_INIT = -1;
	public static final int FLAG_UPDATE_ALWAYS = 2;
	public static final int FLAG_UPDATE_PRE = 5;
	public static final int FLAG_UPDATE_POST = 7;
	public static final int FLAG_RENDER = 10;
	public static final int FLAG_DISPOSE = -2;

	private int flag;
	private Method method;
	private Module module;
	private boolean hasRun;

	/**
	 * Creates a new handler.
	 *
	 * @param flag The handler flag.
	 * @param method The reflect method to call when run.
	 * @param module The module to run the method from.
	 */
	protected Handler(int flag, Method method, Module module) {
		this.flag = flag;
		this.method = method;
		this.module = module;
		this.hasRun = false;
	}

	/**
	 * Run the function method for this handler.
	 */
	protected void run() {
		if (method != null && module != null) {
			try {
				method.invoke(module);
			} catch (IllegalAccessException | InvocationTargetException e) {
				FlounderLogger.get().error("Handler could not call method: " + method.toString());
				FlounderLogger.get().exception(e);
				Framework.get().requestClose(true);
			}

			hasRun = true;
		}
	}

	/**
	 * Gets the update que flag for implemented handler, lower runs first. This value is used to order the handler run call in the framework.
	 *
	 * @return The flag for the implemented handler.
	 */
	protected int getFlag() {
		return flag;
	}

	/**
	 * Gets if this handler has run at least once.
	 *
	 * @return If the handler has run.
	 */
	protected boolean hasRun() {
		return hasRun;
	}

	/**
	 * Represents a method that represents a handler function.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface Function {
		int value();
	}
}
