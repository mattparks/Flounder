package flounder.engine;

import flounder.maths.vectors.*;

/**
 * Represents a game entry point for the engine to run from.
 */
public abstract class IGame {
	public final Vector3f focusPosition;
	public final Vector3f focusRotation;
	public boolean gamePaused;
	public float screenBlur;

	/**
	 * Creates a new game entry point.
	 */
	public IGame() {
		focusPosition = new Vector3f(0.0f, 0.0f, 0.0f);
		focusRotation = new Vector3f(0.0f, 0.0f, 0.0f);
		gamePaused = false;
		screenBlur = 0.0f;
	}

	public Vector3f getFocusPosition() {
		return focusPosition;
	}

	public Vector3f getFocusRotation() {
		return focusRotation;
	}

	public boolean isGamePaused() {
		return gamePaused;
	}

	public float getScreenBlur() {
		return screenBlur;
	}

	/**
	 * Updates the current engines game settings.
	 *
	 * @param focusPosition The position of the object the camera focuses on.
	 * @param focusRotation The rotation of the object the camera focuses on.
	 * @param gamePaused If game paused. Used to stop inputs to camera in menus.
	 * @param screenBlur The factor (-1 to 1) by where the screen will be blurred from on the paused screen.
	 */
	public void updateGame(final Vector3f focusPosition, final Vector3f focusRotation, final boolean gamePaused, final float screenBlur) {
		this.focusPosition.set(focusPosition);
		this.focusRotation.set(focusRotation);
		this.gamePaused = gamePaused;
		this.screenBlur = screenBlur;
	}

	/**
	 * Used to initialise the game, can be used to generate the world.
	 */
	public abstract void init();

	/**
	 * Used to update internal game objects and values. (call the {@link #updateGame(Vector3f, Vector3f, boolean, float) updateGame} method at the end of the function.
	 */
	public abstract void update();

	/**
	 * Cleans up all of the game objects. Should be called when the game closes.
	 */
	public abstract void dispose();
}
