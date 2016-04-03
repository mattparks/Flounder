package flounder.engine;

import flounder.devices.*;
import flounder.engine.profiling.*;
import flounder.fonts.*;
import flounder.guis.*;
import flounder.loaders.*;
import flounder.maths.matrices.*;
import flounder.processing.*;
import flounder.processing.glProcessing.*;
import flounder.textures.*;

/**
 * Deals with much of the initializing, updating, and cleaning up of the engine.
 */
public class FlounderEngine {
	private static boolean initialized;
	private static IModule module;
	private static ProfileTab profileTab;

	private static float targetFPS;
	private static float currentFrameTime;
	private static float lastFrameTime;
	private static long timerStart;
	private static float frames;
	private static float updates;
	private static float delta;
	private static float time;

	/**
	 * @param displayWidth The window width in pixels.
	 * @param displayHeight The window height in pixels.
	 * @param displayTitle The window title.
	 * @param targetFPS The engines target frames per second.
	 * @param displayVSync If the window will use vSync..
	 * @param displayAntialiasing If OpenGL will use altialiasing.
	 * @param displaySamples How many MFAA samples should be done before swapping display buffers. Zero disables multisampling. GLFW_DONT_CARE means no preference.
	 * @param displayFullscreen If the window will start fullscreen.
	 */
	public static void preinit(final int displayWidth, final int displayHeight, final String displayTitle, final float targetFPS, final boolean displayVSync, final boolean displayAntialiasing, final int displaySamples, final boolean displayFullscreen) {
		FlounderEngine.targetFPS = targetFPS;
		FlounderProfiler.init(displayTitle + " Profiler");
		ManagerDevices.init(displayWidth, displayHeight, displayTitle, displayVSync, displayAntialiasing, displaySamples, displayFullscreen);
		profileTab = new ProfileTab("Engine");
		FlounderProfiler.addTab(profileTab);
	}

	/**
	 * Carries out any necessary initializations of the engine.
	 *
	 * @param module The module for the engine to run off of.
	 */
	public static void init(final IModule module) {
		if (!initialized) {
			currentFrameTime = 0.0f;
			lastFrameTime = 0.0f;
			timerStart = System.currentTimeMillis() + 1000;
			frames = 0.0f;
			updates = 0.0f;
			delta = 0.0f;
			time = 0.0f;

			FontManager.init();
			// GuiManager.init();
			(FlounderEngine.module = module).init();
			initialized = true;
		}
	}

	public static void run() {
		while (ManagerDevices.getDisplay().isOpen()) {
			boolean render = false;

			{
				render = true;
				update();
				addProfileValues();
				FlounderProfiler.update();

				// Updates static delta and times.
				currentFrameTime = ManagerDevices.getDisplay().getTime() / 1000.0f;
				delta = currentFrameTime - lastFrameTime;
				lastFrameTime = currentFrameTime;
				time += delta;

				// Prints out current engine update and frame stats.
				if (System.currentTimeMillis() - timerStart > 1000) {
					Logger.log(updates + "ups, " + frames + "fps.");
					timerStart += 1000;
					updates = 0;
					frames = 0;
				}
			}

			if (render) {
				render();
			} else {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static void addProfileValues() {
		profileTab.addLabel("Target FPS", targetFPS);
		profileTab.addLabel("Current Frame Time", currentFrameTime);
		profileTab.addLabel("Last Frame Time", lastFrameTime);
		profileTab.addLabel("Frames Per Second", frames);
		profileTab.addLabel("Updates Per Second", updates);
		profileTab.addLabel("Delta", delta);
		profileTab.addLabel("Time", time);
	}

	/**
	 * Updates many engine systems before every frame.
	 */
	public static void update() {
		ManagerDevices.preRender(delta);
		module.update();
		GuiManager.updateGuis();
		updates++;
	}

	/**
	 * Renders the engines master renderer and carries out OpenGL request calls.
	 */
	public static void render() {
		module.render();
		ManagerDevices.postRender();
		GlRequestProcessor.dealWithTopRequests();
		frames++;
	}

	/**
	 * @return The engines camera implementation.
	 */
	public static ICamera getCamera() {
		return module.getCamera();
	}

	/**
	 * @return The projection matrix used in the current scene renderObjects.
	 */
	public static Matrix4f getProjectionMatrix() {
		return module.getRendererMaster().getProjectionMatrix();
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

	/**
	 * Deals with closing down the engine and all necessary systems.
	 */
	public static void dispose() {
		if (initialized) {
			Loader.dispose();
			RequestProcessor.dispose();
			GlRequestProcessor.completeAllRequests();
			TextureManager.dispose();

			module.dispose();
			FlounderProfiler.dispose();
			ManagerDevices.dispose();
			initialized = false;
		}
	}
}
