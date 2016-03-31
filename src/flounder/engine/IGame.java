package flounder.engine;

import flounder.maths.vectors.*;

/**
 * Represents a game entry point for the engine to run from.
 */
public abstract class IGame {
	public Vector3f m_focusPosition;
	public Vector3f m_focusRotation;
	public boolean m_gamePaused;
	public float m_screenBlur;

	/**
	 *
	 */
	public IGame() {
		m_focusPosition = new Vector3f(0.0f, 0.0f, 0.0f);
		m_focusRotation = new Vector3f(0.0f, 0.0f, 0.0f);
		m_gamePaused = false;
		m_screenBlur = 0.0f;
	}

	public Vector3f getFocusPosition() {
		return m_focusPosition;
	}

	public Vector3f getFocusRotation() {
		return m_focusRotation;
	}

	public boolean isGamePaused() {
		return m_gamePaused;
	}

	public float getScreenBlur() {
		return m_screenBlur;
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
		m_focusPosition.set(focusPosition);
		m_focusRotation.set(focusRotation);
		m_gamePaused = gamePaused;
		m_screenBlur = screenBlur;
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
