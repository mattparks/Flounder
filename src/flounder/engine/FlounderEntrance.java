package flounder.engine;

import flounder.engine.entrance.*;
import flounder.maths.vectors.*;
import flounder.resources.*;

/**
 * A abstract class used to build a game out of.
 */
public abstract class FlounderEntrance extends FlounderEngine {
	protected ICamera camera;
	protected IRendererMaster renderer;
	protected IManagerGUI managerGUI;

	protected Vector3f focusPosition;
	protected Vector3f focusRotation;
	protected boolean gamePaused;
	protected float screenBlur;

	/**
	 * The main entrance class used to create a game/program running from the Flounder Engine.
	 *
	 * @param camera The main camera to use.
	 * @param renderer The master renderer to render with.
	 * @param managerGUI The manager for the implementation for GUIs.
	 * @param width The window width in pixels.
	 * @param height The window height in pixels.
	 * @param title The window title.
	 * @param icons A list of icons to load for the window.
	 * @param vsync If the window will use vSync..
	 * @param antialiasing If OpenGL will use antialiasing.
	 * @param samples How many MFAA samples should be done before swapping buffers. Zero disables multisampling. GLFW_DONT_CARE means no preference.
	 * @param fullscreen If the window will start fullscreen.
	 * @param fpsLimit The maximum FPS the engine can render at.
	 */
	public FlounderEntrance(ICamera camera, IRendererMaster renderer, IManagerGUI managerGUI, int width, int height, String title, MyFile[] icons, boolean vsync, boolean antialiasing, int samples, boolean fullscreen, int fpsLimit) {
		super(width, height, title, icons, vsync, antialiasing, samples, fullscreen, fpsLimit);
		this.camera = camera;
		this.renderer = renderer;
		this.managerGUI = managerGUI;

		super.loadEntrance(this);

		this.focusPosition = new Vector3f();
		this.focusRotation = new Vector3f();
		this.gamePaused = false;
		this.screenBlur = 0.0f;
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
		FlounderEngine.getCamera().update(focusPosition, focusRotation, gamePaused);
	}

	public abstract void init();

	public abstract void update();

	public abstract void profile();

	public abstract void dispose();
}
