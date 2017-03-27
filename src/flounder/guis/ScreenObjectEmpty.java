package flounder.guis;

import flounder.maths.vectors.*;

/**
 * A gui object that has a empty update and delete method.
 */
public class ScreenObjectEmpty extends ScreenObject {
	/**
	 * Creates a new empty screen gui container.
	 */
	public ScreenObjectEmpty(ScreenObject parent, Vector2f position, Vector2f dimensions, boolean inScreenCoords) {
		super(parent, position, dimensions);
		super.setInScreenCoords(inScreenCoords);
	}

	@Override
	public void updateObject() {
	}

	@Override
	public void deleteObject() {
	}
}
