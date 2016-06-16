package flounder.engine;

import flounder.devices.*;
import flounder.fonts.*;
import flounder.guis.*;
import flounder.loaders.*;
import flounder.maths.*;
import flounder.maths.matrices.*;
import flounder.processing.*;
import flounder.processing.glProcessing.*;
import flounder.profiling.*;
import flounder.textures.*;

/**
 * Deals with much of the initializing, updating, and cleaning up of the engine.
 */
public class FlounderEngine implements Runnable {
	private static FlounderDevices devices;
	private static IModule module;

	private static boolean initialized;

	private static float targetFPS;
	private static Delta updateDelta;
	private static Delta framesDelta;

	private static Timer logTimer;
	private static Timer updateTimer;
	private static Timer framesTimer;

	/**
	 * Carries out initializations for basic engine components like the profiler, display and then the engine. Call {@link #startEngine(FontType)} immediately after this.
	 *
	 * @param module The module for the engine to run off of.
	 * @param displayWidth The window width in pixels.
	 * @param displayHeight The window height in pixels.
	 * @param displayTitle The window title.
	 * @param targetFPS The engines target frames per second.
	 * @param displayVSync If the window will use vSync..
	 * @param displayAntialiasing If OpenGL will use altialiasing.
	 * @param displaySamples How many MFAA samples should be done before swapping display buffers. Zero disables multisampling. GLFW_DONT_CARE means no preference.
	 * @param displayFullscreen If the window will start fullscreen.
	 */
	public FlounderEngine(IModule module, int displayWidth, int displayHeight, String displayTitle, float targetFPS, boolean displayVSync, boolean displayAntialiasing, int displaySamples, boolean displayFullscreen) {
		if (!initialized) {
			FlounderEngine.targetFPS = targetFPS;
			FlounderProfiler.init(displayTitle + " Profiler");
			FlounderProfiler.addTab("Engine");
			devices = new FlounderDevices(displayWidth, displayHeight, displayTitle, displayVSync, displayAntialiasing, displaySamples, displayFullscreen);

			updateDelta = new Delta();
			framesDelta = new Delta();

			logTimer = new Timer(1.0f);
			updateTimer = new Timer(1.0f / 60.0f);
			framesTimer = new Timer(1.0f / targetFPS);

			FlounderEngine.module = module;
			initialized = true;
		}
	}

	/**
	 * @return The engines camera implementation.
	 */
	public static ICamera getCamera() {
		return module.getCamera();
	}

	/**
	 * @return The modules current master renderer.
	 */
	public static IRendererMaster getMasterRenderer() {
		return module.getRendererMaster();
	}

	/**
	 * @return The projection matrix used in the current scene renderObjects.
	 */
	public static Matrix4f getProjectionMatrix() {
		return module.getRendererMaster().getProjectionMatrix();
	}

	/**
	 * @return If the game currently paused?
	 */
	public static boolean isGamePaused() {
		return module.getGame().isGamePaused();
	}

	/**
	 * @return How much is the screen blurred (used for pause screens).
	 */
	public static float getScreenBlur() {
		return module.getGame().getScreenBlur();
	}

	/**
	 * Sets the engines target FPS.
	 *
	 * @param targetFPS The new target FPS.
	 */
	public static void setTargetFPS(float targetFPS) {
		FlounderEngine.targetFPS = targetFPS;
		framesTimer = new Timer((1.0f / targetFPS) * 1000.0f);
	}

	/**
	 * @return How many FPS the engine is currently getting.
	 */
	public static float getFPS() {
		return Maths.roundToPlace(1.0f / framesDelta.getDelta(), 2);
	}

	/**
	 * @return How many UPS the engine is currently getting.
	 */
	public static float getUPS() {
		return Maths.roundToPlace(1.0f / updateDelta.getDelta(), 2);
	}

	/**
	 * @return The delta between updates.
	 */
	public static float getDelta() {
		return framesDelta.getDelta();
	}

	/**
	 * @return The current engine time (all delta added up).
	 */
	public static float getTime() {
		return Maths.roundToPlace((framesDelta.getTime() + updateDelta.getTime()) / 2.0f, 2);
	}

	/**
	 * Starts the engine and sets the default family to be used when creating a font. Call {@link #closeEngine()} immediately after this.
	 *
	 * @param defaultFontType The default font family to use when creating texts, this can be overridden with Text.setFont().
	 */
	public void startEngine(FontType defaultFontType) {
		if (defaultFontType != null) {
			TextBuilder.DEFAULT_TYPE = defaultFontType;
		}

		module.init();
		this.run();
	}

	/**
	 * Runs the engines main game loop. Call {@link #closeEngine()} right after running to close the engine.
	 */
	@Override
	public void run() {
		while (initialized && FlounderDevices.getDisplay().isOpen()) {
			// Updates the engine.
			if (updateTimer.isPassedTime()) {
				update();
				updateTimer.resetStartTime();
			}

			// Prints out current engine update and frame stats.
			if (logTimer.isPassedTime()) {
				addProfileValues();
				logTimer.resetStartTime();
			}

			// Renders the engine.
			if (framesTimer.isPassedTime()) {
				render();
				framesTimer.resetStartTime();
			}
		}
	}

	private void addProfileValues() {
		FlounderLogger.log(getFPS() + "fps, " + getUPS() + "ups.");

		if (FlounderProfiler.isOpen()) {
			FlounderProfiler.add("Engine", "Target FPS", targetFPS);
			FlounderProfiler.add("Engine", "Frames Per Second", getFPS());
			FlounderProfiler.add("Engine", "Updates Per Second", getUPS());
			FlounderProfiler.add("Engine", "Update Delta", updateDelta.getDelta());
			FlounderProfiler.add("Engine", "Frames Delta", framesDelta.getDelta());
			FlounderProfiler.add("Engine", "Time", getTime());
		}
	}

	/**
	 * Updates many engine systems before every frame.
	 */
	private void update() {
		updateDelta.update();
		devices.run();
		module.update();
		GuiManager.updateGuis();
	}

	/**
	 * Renders the engines master renderer and carries out OpenGL request calls.
	 */
	private void render() {
		framesDelta.update();
		module.render();
		devices.swapToDisplay();
		GlRequestProcessor.dealWithTopRequests();
	}

	/**
	 * Deals with closing down the engine and all necessary systems. Do not run OpenGL after this.
	 */
	public void closeEngine() {
		if (initialized) {
			Loader.dispose();
			RequestProcessor.dispose();
			GlRequestProcessor.completeAllRequests();
			TextureManager.dispose();

			module.dispose();
			FlounderProfiler.dispose();
			FlounderLogger.dispose();
			devices.dispose();
			initialized = false;
		}
	}
}
