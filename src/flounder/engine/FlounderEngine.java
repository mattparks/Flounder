package flounder.engine;

import flounder.devices.*;
import flounder.engine.profiling.*;
import flounder.guis.*;
import flounder.loaders.*;
import flounder.maths.matrices.*;
import flounder.processing.*;
import flounder.processing.glProcessing.*;
import flounder.textures.*;

/**
 * Deals with much of the initializing, updating, and cleaning up of the engine.
 */
public class FlounderEngine implements Runnable {
	private static boolean initialized;
	private static ManagerDevices devices;
	private static IModule module;

	private static float targetFPS;
	private static float currentFrameTime;
	private static float lastFrameTime;
	private static long timerStart;
	private static float frames;
	private static float updates;
	private static float delta;
	private static float time;

	/**
	 * Carries out initializations for basic engine components like the profiler, display and then the engine. Call {@link #run()} immediately after this.
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
	public FlounderEngine(final IModule module, final int displayWidth, final int displayHeight, final String displayTitle, final float targetFPS, final boolean displayVSync, final boolean displayAntialiasing, final int displaySamples, final boolean displayFullscreen) {
		if (!initialized) {
			FlounderEngine.targetFPS = targetFPS;
			FlounderProfiler.init(displayTitle + " Profiler");
			devices = new ManagerDevices(displayWidth, displayHeight, displayTitle, displayVSync, displayAntialiasing, displaySamples, displayFullscreen);

			currentFrameTime = 0.0f;
			lastFrameTime = 0.0f;
			timerStart = System.currentTimeMillis() + 1000;
			frames = 0.0f;
			updates = 0.0f;
			delta = 0.0f;
			time = 0.0f;

			(FlounderEngine.module = module).init();
			initialized = true;
		}
	}

	/**
	 * @return The engines camera implementation.
	 */
	public static ICamera getCamera() {
		return module.getCamera();
	}

	public static IRendererMaster getMasterRenderer() {
		return module.getRendererMaster();
	}

	/**
	 * @return The projection matrix used in the current scene renderObjects.
	 */
	public static Matrix4f getProjectionMatrix() {
		return module.getRendererMaster().getProjectionMatrix();
	}

	public static boolean isGamePaused() {
		return module.getGame().isGamePaused();
	}

	public static float getScreenBlur() {
		return module.getGame().getScreenBlur();
	}

	public static void setTargetFPS(final float targetFPS) {
		FlounderEngine.targetFPS = targetFPS;
	}

	public static float getFPS() {
		return frames;
	}

	public static float getUPS() {
		return updates;
	}

	public static float getDelta() {
		return delta;
	}

	public static float getTime() {
		return time;
	}

	public void startEngine() {
		devices.run();
		this.run();
	}

	/**
	 * Runs the engines main game loop. Call {@link #dispose()} right after running to close the engine.
	 */
	@Override
	public void run() {
		while (initialized && ManagerDevices.getDisplay().isOpen()) {
			devices.run();

			// Updates the engine.
			update();

			// Updates static delta and times.
			currentFrameTime = ManagerDevices.getDisplay().getTime() / 1000.0f;
			delta = currentFrameTime - lastFrameTime;
			lastFrameTime = currentFrameTime;
			time += delta;

			// Prints out current engine update and frame stats.
			if (System.currentTimeMillis() - timerStart > 1000) {
				FlounderLogger.log(updates + "ups, " + frames + "fps.");
				addProfileValues();
				timerStart += 1000;
				updates = 0;
				frames = 0;
			}

			// Renders the engine.
			render();
		}
	}

	private void addProfileValues() {
		if (FlounderProfiler.isOpen()) {
			FlounderProfiler.add("Engine", "Target FPS", targetFPS);
			FlounderProfiler.add("Engine", "Current Frame Time", currentFrameTime);
			FlounderProfiler.add("Engine", "Last Frame Time", lastFrameTime);
			FlounderProfiler.add("Engine", "Frames Per Second", frames);
			FlounderProfiler.add("Engine", "Updates Per Second", updates);
			FlounderProfiler.add("Engine", "Delta", delta);
			FlounderProfiler.add("Engine", "Time", time);
		}
	}

	/**
	 * Updates many engine systems before every frame.
	 */
	private void update() {
		module.update();
		GuiManager.updateGuis();
		updates++;
	}

	/**
	 * Renders the engines master renderer and carries out OpenGL request calls.
	 */
	private void render() {
		module.render();
		devices.swapToDisplay();
		GlRequestProcessor.dealWithTopRequests();
		frames++;
	}

	public void stopEngine() {
		this.dispose();
		devices.dispose();
	}

	/**
	 * Deals with closing down the engine and all necessary systems.
	 */
	public void dispose() {
		if (initialized) {
			Loader.dispose();
			RequestProcessor.dispose();
			GlRequestProcessor.completeAllRequests();
			TextureManager.dispose();

			module.dispose();
			FlounderProfiler.dispose();
			FlounderLogger.dispose();
			initialized = false;
		}
	}
}
