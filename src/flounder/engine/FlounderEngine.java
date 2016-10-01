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

	private static final List<IModule> activeModules = new ArrayList<>();
	private static final List<String> unloggedModules = new ArrayList<>();

	private boolean closedRequested;
	private Delta deltaUpdate;
	private Delta deltaRender;
	private Timer timerUpdate;
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

	/**
	 * Gets if the engine contains a module.
	 *
	 * @param object The module class.
	 *
	 * @return If the engine contains a module.
	 */
	protected static boolean containsModule(Class object) {
		for (IModule m : activeModules) {
			if (m.getClass().getName().equals(object.getName())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Loads a module into a IModule class and gets the instance.
	 *
	 * @param object The module class.
	 *
	 * @return The module instance class.
	 */
	protected static IModule loadModule(Class object) {
		try {
			return ((IModule) object.newInstance()).getInstance();
		} catch (IllegalAccessException | InstantiationException e) {
			System.err.println("IModule class path " + object.getName() + " constructor could not be found!");
			e.printStackTrace();
		}

		return null;
	}

	protected static void registerModule(IModule module) {
		if (module == null || containsModule(module.getClass())) {
			return;
		}

		// Add the module temperaraly.
		activeModules.add(module);

		for (int i = module.getRequires().length - 1; i >= 0; i--) {
			FlounderEngine.registerModule(loadModule(module.getRequires()[i]));
		}

		// Add the module after all required.
		activeModules.remove(module);
		activeModules.add(module);

		// Initialize modules if needed,
		if (instance.initialized && !module.isInitialized()) {
			module.init();
			module.setInitialized(true);
		}

		// Log module data.
		String requires = "";

		for (int i = 0; i < module.getRequires().length; i++) {
			requires += module.getRequires()[i].getSimpleName() + ((i == module.getRequires().length - 1) ? "" : ", ");
		}

		String logOutput = "Registering " + module.getClass().getSimpleName() + ":" + FlounderLogger.ANSI_PURPLE + " (" + (instance.initialized ? "POST_INIT, " : "") + "UPDATE_" + module.getModuleUpdate().name() + ")" + FlounderLogger.ANSI_RESET + ":" + FlounderLogger.ANSI_RED + " Requires(" + requires + ")" + FlounderLogger.ANSI_RESET;

		if (instance.initialized && containsModule(FlounderLogger.class)) {
			if (!unloggedModules.isEmpty()) {
				unloggedModules.forEach(FlounderLogger::log);
			}

			unloggedModules.clear();
			FlounderLogger.log(logOutput);
		} else {
			unloggedModules.add(logOutput);
		}
	}

	/**
	 * Carries out the setup for basic engine components and the engine. Call {@link #startEngine(FontType)} immediately after this.
	 */
	public FlounderEngine() {
		instance = this;

		// Increment revision every fix for the minor version release. Minor version represents the build month. Major incremented every two years OR after major core engine rewrites.
		version = new Version("1.10.01");

		this.closedRequested = false;
		this.deltaUpdate = new Delta();
		this.deltaRender = new Delta();
		this.timerUpdate = new Timer(1.0f / 60.0f);
		this.timerLog = new Timer(1.0f);

		deltaUpdate.update();
		deltaRender.update();

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

	@Override
	public void run() {
		try {
			initEngine();

			while (isRunning()) {
				boolean update = false;
				if (timerUpdate.isPassedTime()) {
					update = true;
					timerUpdate.resetStartTime();
				}
				updateEngine(update, true); // TODO: Only update every 1/60 seconds. Only render when deltaRender is below 1/fpsLimit.
				profileEngine();
			}
		} catch (Exception e) {
			e.printStackTrace();
			FlounderLogger.exception(e);
		} finally {
			disposeEngine();
		}
	}

	private void initEngine() {
		if (!initialized) {
			for (IModule module : activeModules) {
				if (!module.isInitialized()) {
					module.init();
					module.setInitialized(true);
				}
			}

			if (containsModule(FlounderLogger.class)) {
				if (!unloggedModules.isEmpty()) {
					unloggedModules.forEach(FlounderLogger::log);
				}

				unloggedModules.clear();
			}

			entrance.managerGUI.init();
			entrance.renderer.init();
			entrance.camera.init();
			entrance.init();

			initialized = true;
		}
	}

	private void updateEngine(boolean update, boolean render) {
		if (initialized) {
			// Updates the module when needed always.
			activeModules.forEach(module -> {
				if (module.getModuleUpdate().equals(IModule.ModuleUpdate.ALWAYS)) {
					module.run();
				}
			});

			// Updates when needed.
			if (update) {
				// Updates the module when needed before the entrance.
				activeModules.forEach(module -> {
					if (module.getModuleUpdate().equals(IModule.ModuleUpdate.BEFORE_ENTRANCE)) {
						module.run();
					}
				});

				// Updates the engine delta, and entrance.
				deltaUpdate.update();
				entrance.update();

				// Updates the module when needed after the entrance.
				activeModules.forEach(module -> {
					if (module.getModuleUpdate().equals(IModule.ModuleUpdate.AFTER_ENTRANCE)) {
						module.run();
					}
				});

				// Updates the entrances gui manager.
				entrance.managerGUI.update();
			}

			// Renders when needed.
			if (render) {
				deltaRender.update();
				entrance.renderer.render();
				FlounderDisplay.swapBuffers();
			}

			// Sleep a bit after updating or rendering.
			try {
				Thread.sleep(1);
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}
	}

	private void profileEngine() {
		if (FlounderProfiler.isOpen() && timerLog.isPassedTime()) {
			FlounderProfiler.add("Engine", "Running From Jar", runningFromJar);
			FlounderProfiler.add("Engine", "Save Folder", roamingFolder.getPath());

			for (IModule module : activeModules) {
				module.profile();
			}

			entrance.profile();
			// FlounderLogger.log(Maths.roundToPlace(1.0f / getDelta(), 2) + "fps");
			timerLog.resetStartTime();
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

	/**
	 * Gets the delta (seconds) between updates.
	 *
	 * @return The deltaRender between updates.
	 */
	public static float getDelta() {
		return instance.deltaUpdate.getDelta();
	}

	/**
	 * Gets the current engine time (all delta added up).
	 *
	 * @return The current engine time.
	 */
	public static float getDeltaTime() {
		return instance.deltaUpdate.getTime();
	}

	/**
	 * Gets the delta (seconds) between renders.
	 *
	 * @return The delta between renders.
	 */
	public static float getDeltaRender() {
		return instance.deltaRender.getDelta();
	}

	/**
	 * Gets the current engine time (all delta added up).
	 *
	 * @return The current engine time.
	 */
	public static float getDeltaRenderTime() {
		return instance.deltaRender.getTime();
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
				module.setInitialized(false);
			}

			activeModules.clear();
			initialized = false;
		}
	}
}
