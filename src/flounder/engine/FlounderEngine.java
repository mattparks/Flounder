package flounder.engine;

import flounder.devices.*;
import flounder.engine.implementation.*;
import flounder.fonts.*;
import flounder.guis.*;
import flounder.loaders.*;
import flounder.logger.*;
import flounder.maths.matrices.*;
import flounder.physics.renderer.*;
import flounder.processing.*;
import flounder.profiling.*;
import flounder.textures.*;

/**
 * Deals with much of the initializing, updating, and cleaning up of the engine.
 */
public class FlounderEngine extends Thread implements IModule {
	private static FlounderEngine instance;

	private Version version;

	private FlounderDevices devices;
	private FlounderProcessors processors;
	private FlounderLoader loader;
	private FlounderTextures textures;
	private FlounderAABBs AABBs;
	private FlounderLogger logger;
	private FlounderProfiler profiler;

	private Implementation implementation;

	/**
	 * Carries out the setup for basic engine components and the engine. Call {@link #startEngine(FontType)} immediately after this.
	 *
	 * @param implementation The game implementation of the engine.
	 * @param width The window width in pixels.
	 * @param height The window height in pixels.
	 * @param title The window title.
	 * @param vsync If the window will use vSync..
	 * @param antialiasing If OpenGL will use antialiasing.
	 * @param samples How many MFAA samples should be done before swapping buffers. Zero disables multisampling. GLFW_DONT_CARE means no preference.
	 * @param fullscreen If the window will start fullscreen.
	 */
	public FlounderEngine(Implementation implementation, int width, int height, String title, boolean vsync, boolean antialiasing, int samples, boolean fullscreen) {
		instance = this;

		// Increment revision every git commit. Minor version represents the build month. Major incremented every two years OR after major core engine rewrites.
		version = new Version("3.6.3");

		this.devices = new FlounderDevices(width, height, title, vsync, antialiasing, samples, fullscreen);
		this.processors = new FlounderProcessors();
		this.loader = new FlounderLoader();
		this.textures = new FlounderTextures();
		this.AABBs = new FlounderAABBs();
		this.logger = new FlounderLogger();
		this.profiler = new FlounderProfiler(title + " Profiler");

		this.implementation = implementation;
	}

	/**
	 * Starts the engine!
	 *
	 * @param defaultType Sets the default font family (nullable).
	 */
	public void startEngine(FontType defaultType) {
		if (defaultType != null) {
			TextBuilder.DEFAULT_TYPE = defaultType;
		}

		start();
	}

	@Override
	public void init() {
		logger.init();
		profiler.init();
		devices.init();
		processors.init();
		textures.init();
		AABBs.init();
		implementation.init();

		// Opens the profiler if not running from jar.
		profiler.toggle(!logger.ALLOW_LOUD_LOGS);
	}

	@Override
	public void run() {
		try {
			init();

			while (isRunning()) {
				update();
				profile();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.exception(e);
		} finally {
			dispose();
		}
	}

	@Override
	public void update() {
		devices.update();

		loader.update();
		textures.update();
		processors.update();
		GuiManager.updateGuis();

		implementation.update();

		AABBs.update();
		logger.update();
		profiler.update();

		devices.swapBuffers();
	}

	@Override
	public void profile() {
		if (FlounderEngine.getProfiler().isOpen()) {
			devices.profile();
			processors.profile();
			implementation.profile();
			AABBs.profile();
			loader.profile();
			textures.profile();
			logger.profile();
			profiler.profile();
		}
	}

	/**
	 * Gets the engines current version.
	 *
	 * @return The engines current version.
	 */
	public static Version getVersion() {
		return instance.version;
	}

	/**
	 * Gets the engines current device manager.
	 *
	 * @return The engines current device manager.
	 */
	public static FlounderDevices getDevices() {
		return instance.devices;
	}

	/**
	 * Gets the engines current profiler.
	 *
	 * @return The engines current profiler.
	 */
	public static FlounderProfiler getProfiler() {
		return instance.profiler;
	}

	/**
	 * Gets the engines current logger.
	 *
	 * @return The engines current logger.
	 */
	public static FlounderLogger getLogger() {
		return instance.logger;
	}

	/**
	 * Gets the engines current OpenGL loader.
	 *
	 * @return The engines current OpenGL loader.
	 */
	public static FlounderLoader getLoader() {
		return instance.loader;
	}

	/**
	 * Gets the engines current request processor.
	 *
	 * @return The engines current request processor.
	 */
	public static FlounderProcessors getProcessors() {
		return instance.processors;
	}

	/**
	 * Gets the engines current texture manager.
	 *
	 * @return The engines current texture manager.
	 */
	public static FlounderTextures getTextures() {
		return instance.textures;
	}

	/**
	 * Gets the engines current AABB renderer manager.
	 *
	 * @return The engines current AABB renderer manager.
	 */
	public static FlounderAABBs getAABBs() {
		return instance.AABBs;
	}

	/**
	 * Gets the engines camera implementation.
	 *
	 * @return The engines camera implementation.
	 */
	public static ICamera getCamera() {
		return instance.implementation.getCamera();
	}

	/**
	 * Gets the modules current master renderer.
	 *
	 * @return The modules current master renderer.
	 */
	public static IRendererMaster getMasterRenderer() {
		return instance.implementation.getRendererMaster();
	}

	/**
	 * Gets the projection matrix used in the current scene renderObjects.
	 *
	 * @return The projection matrix used in the current scene renderObjects.
	 */
	public static Matrix4f getProjectionMatrix() {
		return instance.implementation.getRendererMaster().getProjectionMatrix();
	}

	/**
	 * Gets the delta (seconds) between updates.
	 *
	 * @return The delta between updates.
	 */
	public static float getDelta() {
		return instance.implementation.getDelta();
	}

	/**
	 * Gets the current engine time (all delta added up).
	 *
	 * @return The current engine time.
	 */
	public static float getDeltaTime() {
		return instance.implementation.getDeltaTime();
	}

	/**
	 * Gets if the engine still running?
	 *
	 * @return Is the engine still running?
	 */
	public static boolean isRunning() {
		return instance.implementation.isRunning();
	}

	/**
	 * Gets the current screen blur factor.
	 *
	 * @return The current screen blur factor.
	 */
	public static float getScreenBlur() {
		return instance.implementation.getGame().getScreenBlur();
	}

	/**
	 * Gets if the game currently paused.
	 *
	 * @return Is the game currently paused?
	 */
	public static boolean isGamePaused() {
		return instance.implementation.getGame().isGamePaused();
	}

	/**
	 * Requests the gameloop to stop and the game to exit.
	 */
	public static void requestClose() {
		instance.implementation.requestClose();
	}

	@Override
	public void dispose() {
		processors.dispose();
		loader.dispose();
		AABBs.dispose();
		textures.dispose();
		implementation.dispose();
		devices.dispose();
		profiler.dispose();
		logger.dispose();
	}
}
