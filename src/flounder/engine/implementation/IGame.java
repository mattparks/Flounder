package flounder.engine.implementation;

import flounder.engine.*;
import flounder.maths.vectors.*;

/**
 * Represents a game entry point for the engine to run from.
 */
public abstract class IGame {
	public Vector3f focusPosition;
	public Vector3f focusRotation;
	public boolean gamePaused;
	public float screenBlur;

	/**
	 * Creates a new game entry point.
	 */
	public IGame() {
		focusPosition = new Vector3f();
		focusRotation = new Vector3f();
		gamePaused = false;
		screenBlur = 0.0f;
	}

	/**
	 * @return The games focus position.
	 */
	public Vector3f getFocusPosition() {
		return focusPosition;
	}

	/**
	 * @return The games focus rotation.
	 */
	public Vector3f getFocusRotation() {
		return focusRotation;
	}

	/**
	 * @return Is the game currently paused?
	 */
	public boolean isGamePaused() {
		return gamePaused;
	}

	/**
	 * @return The current screen blur factor.
	 */
	public float getScreenBlur() {
		return screenBlur;
	}

	/**
	 * Updates the current engines game settings.
	 *
	 * @param focusPosition The position of the object the camera focuses on.
	 * @param focusRotation The rotation of the object the camera focuses on.
	 */
	public void update(Vector3f focusPosition, Vector3f focusRotation) {
		this.focusPosition.set(focusPosition);
		this.focusRotation.set(focusRotation);
		this.gamePaused = FlounderEngine.getManagerGUI().isMenuIsOpen();
		this.screenBlur = FlounderEngine.getManagerGUI().getBlurFactor();
	}

	/**
	 * Used to initialise the game, can be used to generate the world.
	 */
	public abstract void init();

	/**
	 * Used to update internal game objects and values. (call the {@link #update(Vector3f, Vector3f) updateGame} method at the end of the function.
	 */
	public abstract void update();

	/**
	 * Cleans up all of the game objects. Should be called when the game closes.
	 */
	public abstract void dispose();
}
