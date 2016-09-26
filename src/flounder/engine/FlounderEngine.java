package flounder.engine;

import flounder.devices.*;
import flounder.engine.entrance.*;
import flounder.fonts.*;
import flounder.logger.*;
import flounder.maths.*;
import flounder.maths.Timer;
import flounder.maths.matrices.*;
import flounder.profiling.*;
import flounder.resources.*;

import java.io.*;
import java.util.*;

/**
 * Deals with much of the initializing, updating, and cleaning up of the engine.
 */
public class FlounderEngine extends Thread {
	private static FlounderEngine instance;

	private Version version;

	private FlounderEntrance entrance;

	private static final List<IModule> unregistedModules = new ArrayList<>();
	private static final List<IModule> activeModules = new ArrayList<>();

	private int fpsLimit;
	private boolean closedRequested;
	private Delta delta;
	private Timer timerLog;

	private boolean initialized;
	private static boolean runningFromJar;
	private static MyFile roamingFolder;

	/**
	 * Called before the engine loads, used to setup roaming folders and other statics.
	 *
	 * @param gameName The games name, used to set the roaming save folder.
	 */
	public static void loadEngineStatics(String gameName) {
		runningFromJar = FlounderEngine.class.getResource("/" + FlounderEngine.class.getName().replace('.', '/') + ".class").toString().startsWith("jar:");
		String saveDir;

		if (System.getProperty("os.name").contains("Windows")) {
			saveDir = System.getenv("APPDATA");
		} else {
			saveDir = System.getProperty("user.home");
		}

		roamingFolder = new MyFile(saveDir, "." + gameName);
		File saveDirectory = new File(saveDir + "/." + gameName + "/");

		if (!saveDirectory.exists()) {
			System.out.println("Creating directory: " + saveDirectory);

			try {
				saveDirectory.mkdir();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}
	}

	public static void registerModule(IModule module) {
		unregistedModules.add(module);
		String data = "";
		for (int i = 0; i < module.getRequires().length; i++) {
			data += module.getRequires()[i] + ", ";
		}
		System.out.println("[" + module.getClass().getName() + "]: " + data);
	}

	/**
	 * Carries out the setup for basic engine components and the engine. Call {@link #startEngine(FontType)} immediately after this.
	 *
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
	public FlounderEngine(int width, int height, String title, MyFile[] icons, boolean vsync, boolean antialiasing, int samples, boolean fullscreen, int fpsLimit) {
		instance = this;

		// Increment revision every fix for the minor version release. Minor version represents the build month. Major incremented every two years OR after major core engine rewrites.
		version = new Version("1.09.22");

		this.fpsLimit = fpsLimit;
		this.closedRequested = false;
		this.delta = new Delta();
		this.timerLog = new Timer(1.0f);

		this.initialized = false;
	}

	protected void loadEntrance(FlounderEntrance entrance) {
		this.entrance = entrance;
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

		run();
	}

	private void initEngine() {
		if (!initialized) {
			for (IModule module : activeModules) {
				module.init();
			}

			entrance.managerGUI.init();
			entrance.renderer.init();
			entrance.camera.init();
			entrance.init();

			initialized = true;
		}
	}

	@Override
	public void run() {
		try {
			initEngine();

			while (isRunning()) {
				updateEngine();
				profileEngine();
			}
		} catch (Exception e) {
			e.printStackTrace();
			FlounderLogger.exception(e);
		} finally {
			disposeEngine();
		}
	}

	private void updateEngine() {
		if (initialized) {

			{
				delta.update();
				entrance.update();

				if (timerLog.isPassedTime()) {
					FlounderLogger.log(Maths.roundToPlace(1.0f / getDelta(), 2) + "fps");
					timerLog.resetStartTime();
				}

				entrance.renderer.render();
				entrance.managerGUI.update();
			}

			FlounderDisplay.swapBuffers();
		}
	}

	private void profileEngine() {
		if (FlounderProfiler.isOpen()) {
			FlounderProfiler.add("Engine", "Running From Jar", runningFromJar);
			FlounderProfiler.add("Engine", "Save Folder", roamingFolder.getPath());

			entrance.profile();
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
	 * Gets the engines camera implementation.
	 *
	 * @return The engines camera implementation.
	 */
	public static ICamera getCamera() {
		return instance.entrance.camera;
	}

	public static void setCamera(ICamera camera) {
		instance.entrance.camera = camera;
	}

	/**
	 * Gets the modules current master renderer.
	 *
	 * @return The modules current master renderer.
	 */
	public static IRendererMaster getMasterRenderer() {
		return instance.entrance.renderer;
	}

	/**
	 * Gets the modules current GUI manager.
	 *
	 * @return The modules current GUI manager.
	 */
	public static IManagerGUI getManagerGUI() {
		return instance.entrance.managerGUI;
	}

	/**
	 * Gets the projection matrix used in the current scene renderObjects.
	 *
	 * @return The projection matrix used in the current scene renderObjects.
	 */
	public static Matrix4f getProjectionMatrix() {
		return instance.entrance.renderer.getProjectionMatrix();
	}

	public static void setTargetFPS(int fpsLimit) {
		instance.fpsLimit = fpsLimit;
	}

	public static int getTargetFPS() {
		return instance.fpsLimit;
	}

	/**
	 * Gets the delta (seconds) between updates.
	 *
	 * @return The delta between updates.
	 */
	public static float getDelta() {
		return instance.delta.getDelta();
	}

	/**
	 * Gets the current engine time (all delta added up).
	 *
	 * @return The current engine time.
	 */
	public static float getDeltaTime() {
		return instance.delta.getTime();
	}

	/**
	 * Gets if the engine still running?
	 *
	 * @return Is the engine still running?
	 */
	public static boolean isRunning() {
		return !instance.closedRequested && !FlounderDisplay.isClosed();
	}

	/**
	 * Gets the current screen blur factor.
	 *
	 * @return The current screen blur factor.
	 */
	public static float getScreenBlur() {
		return instance.entrance.screenBlur;
	}

	/**
	 * Gets if the game currently paused.
	 *
	 * @return Is the game currently paused?
	 */
	public static boolean isGamePaused() {
		return instance.entrance.gamePaused;
	}

	/**
	 * Requests the gameloop to stop and the game to exit.
	 */
	public static void requestClose() {
		instance.closedRequested = true;
	}

	/**
	 * Gets if the engine is currently initialized.
	 *
	 * @return Is the engine is currently initialized?
	 */
	public static boolean isInitialized() {
		return instance.initialized;
	}

	/**
	 * Gets if the engine is currently running from a jae.
	 *
	 * @return Is the engine is currently running from a jae?
	 */
	public static boolean isRunningFromJar() {
		return runningFromJar;
	}

	/**
	 * Gets the file that goes to the roaming folder.
	 *
	 * @return The roaming folder file.
	 */
	public static MyFile getRoamingFolder() {
		return roamingFolder;
	}

	private void disposeEngine() {
		if (initialized) {
			entrance.renderer.dispose();
			entrance.dispose();

			for (IModule module : activeModules) {
				module.dispose();
			}

			initialized = false;
		}
	}
}
