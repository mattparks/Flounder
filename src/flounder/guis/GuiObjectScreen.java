package flounder.guis;

import flounder.maths.vectors.*;

/**
 * A gui object that has no parent and takes up the entire screen. This is used to hold all other components in the gui engine.
 */
public class GuiObjectScreen extends ScreenObject {
	/**
	 * Creates a new screen gui container.
	 */
	public GuiObjectScreen() {
		super(null, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);
	}

	@Override
	public void updateObject() {
	}

	@Override
	public void deleteObject() {
	}
}
