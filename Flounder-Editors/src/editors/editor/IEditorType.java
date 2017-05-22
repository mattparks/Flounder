package editors.editor;

import flounder.framework.*;

/**
 * A extension used with {@link EditorsManager} to define a editor type.
 */
public abstract class IEditorType extends Extension {
	/**
	 * Creates a new editor type.
	 *
	 * @param requires The classes that are extra requirements for this implementation.
	 */
	public IEditorType(Class... requires) {
		super(EditorsManager.class, requires);
	}

	/**
	 * Run when initializing the editor type.
	 */
	public abstract void init();

	/**
	 * Run when updating the editor type.
	 */
	public abstract void update();

	/**
	 * Run when profiling the editor type.
	 */
	public abstract void profile();

	/**
	 * Run when disposing the editor type.
	 */
	public abstract void dispose();

	@Override
	public abstract boolean isActive();
}
