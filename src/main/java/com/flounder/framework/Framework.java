package com.flounder.framework;

import com.flounder.framework.updater.*;
import com.flounder.logger.*;
import com.flounder.resources.*;
import com.flounder.standards.*;

import java.io.*;
import java.util.*;

/**
 * A framework used for simplifying the creation of complicated Java applications. By using flexible Module loading and Extension injecting, it allows the engine to be used for Networking, Imaging, AIs, Games, and many more applications.
 * Start off by creating a new Framework object in your main thread, using Extensions in the constructor. By using Extensions: Modules can be required and therefor loaded into the framework.
 * Implementing interfaces like {@link Standard} with your extension can allow you do task specific things with your Extensions. After creating your Framework object call {@link #run()} to start.
 */
public class Framework {
	private static Framework INSTANCE = null;

	private String unlocalizedName;

	private Version version;
	private IUpdater updater;

	private List<Module> modules = new ArrayList<>();
	private List<Module> overrides = new ArrayList<>();
	private boolean initialized;
	private boolean running;
	private boolean error;
	private int fpsLimit;

	/**
	 * Carries out the setup for basic framework components and the framework. Call {@link #run()} after creating a instance.
	 *
	 * @param unlocalizedName The name to be used when determining where the roaming save files are saved.
	 * @param updater The definition for how the framework will run.
	 * @param fpsLimit The limit to FPS, (-1 disables limits).
	 * @param extensions The extensions to load for the framework.
	 */
	public Framework(String unlocalizedName, IUpdater updater, int fpsLimit, Extension[] extensions) {
		// Sets the static object to this new one.
		Framework.INSTANCE = this;

		// Sets the instances name.
		this.unlocalizedName = unlocalizedName;

		// Increment revision every fix for the minor version release. Minor version represents the build month. Major incremented every two years OR after major core framework rewrites.
		this.version = new Version("19.02.01");

		// Sets the frameworks updater.
		this.updater = updater;

		// Sets up the module and overrides lists.
		this.modules = new ArrayList<>();
		this.overrides = new ArrayList<>();

		// Registers these modules as global, we do this as everyone loves these guys <3
		registerModules(loadModule(FlounderLogger.class));

		// Force registers the extensions, as the framework was null when they were constructed.
		for (Extension extension : extensions) {
			registerModule(loadModule(extension.getModule())).registerExtension(extension);
		}

		this.initialized = false;
		this.running = true;
		this.error = false;
		this.fpsLimit = fpsLimit;
	}

	public void run() {
		try {
			updater.run();
		} catch (Exception e) {
			FlounderLogger.get().exception(e);
			requestClose(true);
		} finally {
			if (error) {
				new LoggerFrame().run();
				System.exit(-1);
			} else {
				updater.dispose();
			}
		}
	}

	public void addOverrides(Module... list) {
		this.overrides.addAll(Arrays.asList(list));
	}

	/**
	 * Runs the handlers using a specific flag.
	 *
	 * @param flag The flag to run from.
	 */
	public void runHandlers(int flag) {
		for (Module module : modules) {
			module.getInstance().runHandler(flag);
		}
	}

	/**
	 * Gets if the framework is currently running from a jar.
	 *
	 * @return Is the framework is currently running from a jar?
	 */
	public boolean isRunningFromJar() {
		return Framework.class.getResource("/" + Framework.class.getName().replace('.', '/') + ".class").toString().startsWith("jar:");
	}

	/**
	 * Gets the file that goes to the roaming folder.
	 *
	 * @return The roaming folder file.
	 */
	public MyFile getRoamingFolder() {
		return getRoamingFolder(unlocalizedName);
	}

	/**
	 * Gets the file that goes to the roaming folder, the unlocalized string overrides the frameworks name in this method.
	 *
	 * @param unlocalized The unlocalized name of the folder.
	 *
	 * @return The roaming folder file.
	 */
	public static MyFile getRoamingFolder(String unlocalized) {
		String saveDir;

		if (System.getProperty("os.name").contains("Windows")) {
			saveDir = System.getenv("APPDATA");
		} else {
			saveDir = System.getProperty("user.home");
		}

		MyFile roamingFolder = new MyFile(saveDir, "." + unlocalized);
		File save = new File(saveDir + "/." + unlocalized + "/");

		if (!save.exists()) {
			System.out.println("Creating directory: " + save);

			try {
				save.mkdir();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}

		return roamingFolder;
	}

	/**
	 * Gets the instances unlocalized name.
	 *
	 * @return The unlocalized name.
	 */
	public String getUnlocalizedName() {
		return unlocalizedName;
	}

	/**
	 * Gets the frameworks current version.
	 *
	 * @return The frameworks current version.
	 */
	public Version getVersion() {
		return version;
	}

	/**
	 * Gets the frameworks updater.
	 *
	 * @return The updater.
	 */
	public IUpdater getUpdater() {
		return updater;
	}

	public List<Module> getModules() {
		return modules;
	}

	public List<Module> getOverrides() {
		return overrides;
	}

	/**
	 * Gets a loaded and registered module from the framework.
	 *
	 * @param object The module class.
	 *
	 * @return The module.
	 */
	public Module getModule(Class object) {
		for (Module module : modules) {
			if (object.isInstance(module)) {
				return module;
			}
		}

		return null;
	}

	/**
	 * Gets a loaded and registered module override from the framework.
	 *
	 * @param parent The module parent class.
	 *
	 * @return The module override.
	 */
	public Module getOverride(Class parent) {
		for (Module module : overrides) {
			if (!module.getClass().equals(parent) && parent.isInstance(module)) {
				return module;
			}
		}

		return null;
	}

	/**
	 * Gets a module instance, or the override to the module.
	 *
	 * @param object The module class.
	 *
	 * @return The module instance.
	 */
	public Module getInstance(Class object) {
		Module override = getOverride(object);
		Module actual = getModule(object);
		return override == null ? actual : override;
	}

	/**
	 * Gets if the framework contains a module.
	 *
	 * @param object The module class.
	 *
	 * @return If the framework contains a module.
	 */
	protected boolean containsModule(Class object) {
		for (Module m : modules) {
			if (m.getClass().getName().equals(object.getName())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Gets if the framework contains all of the modules.
	 *
	 * @param objects The module classes.
	 *
	 * @return If the framework contains all of the modules.
	 */
	protected boolean containsModules(Class... objects) {
		for (Class object : objects) {
			if (!containsModule(object)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Loads a module into a Module class and gets the instance.
	 *
	 * @param object The module class.
	 *
	 * @return The module INSTANCE class.
	 */
	protected Module loadModule(Class object) {
		Module m = getModule(object);

		if (m != null) {
			return m;
		}

		try {
			return ((Module) object.newInstance()).getInstance();
		} catch (IllegalAccessException | InstantiationException e) {
			System.err.println("Module class path " + object.getName() + " constructor could not be found!");
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Loads a list of modules into Module classes and gets all of the instances.
	 *
	 * @param objects The module classes.
	 *
	 * @return The modules instance classes.
	 */
	protected Module[] loadModules(Class... objects) {
		Module[] result = new Module[objects.length];

		for (int i = 0; i < objects.length; i++) {
			result[i] = loadModule(objects[i]);
		}

		return result;
	}

	/**
	 * Registers a module, and initializes if the engine has already started.
	 *
	 * @param module The module to init.
	 *
	 * @return The initialized module.
	 */
	protected Module registerModule(Module module) {
		if (module == null || containsModule(module.getClass())) {
			return module;
		}

		// Add the module temporally.
		modules.add(module);

		// Will load and init required modules if needed.
		if (!containsModules(module.getDependencies())) {
			// Registers all required modules.
			registerModules(loadModules(module.getDependencies()));

			// Add the module to the modules list.
			modules.remove(module);
			modules.add(module);
		}

		// Initialize modules if needed,
		if (initialized && module.hasHandlerRun(Handler.FLAG_INIT)) {
			module.getHandler(Handler.FLAG_INIT).run();
		}

		return module;
	}

	/**
	 * Registers a list of modules, and initializes them if the engine has already started.
	 *
	 * @param list The list of modules to init.
	 */
	protected void registerModules(Module... list) {
		for (Module module : list) {
			registerModule(module);
		}
	}

	/**
	 * Logs all information from a module.
	 */
	public void logModules() {
		// Logs all registered modules.
		for (Module module : modules) {
			StringBuilder requires = new StringBuilder();

			for (int i = 0; i < module.getDependencies().length; i++) {
				requires.append(module.getDependencies()[i].getSimpleName());
				requires.append((i == module.getDependencies().length - 1) ? "" : ", ");
			}

			boolean last = module.equals(modules.get(modules.size() - 1));

			FlounderLogger.get().init("Registering " + module.getClass().getSimpleName() + ": " + FlounderLogger.ANSI_PURPLE + "Requires(" + requires.toString() + ")" + FlounderLogger.ANSI_RESET + (last ? "\n" : ""));
		}
	}

	/**
	 * Gets the added/removed time for the framework (seconds).
	 *
	 * @return The time offset.
	 */
	public float getTimeOffset() {
		return updater.getTimeOffset();
	}

	/**
	 * Sets the time offset for the framework (seconds).
	 *
	 * @param timeOffset The new time offset.
	 */
	public void setTimeOffset(float timeOffset) {
		updater.setTimeOffset(timeOffset);
	}

	/**
	 * Gets the delta (seconds) between updates.
	 *
	 * @return The delta between updates.
	 */
	public float getDelta() {
		return updater.getDelta();
	}

	/**
	 * Gets the delta (seconds) between renders.
	 *
	 * @return The delta between renders.
	 */
	public float getDeltaRender() {
		return updater.getDeltaRender();
	}

	/**
	 * Gets the current time of the framework instance.
	 *
	 * @return The current framework time in seconds.
	 */
	public float getTimeSec() {
		return updater.getTimeSec();
	}

	/**
	 * Gets the current time of the framework instance.
	 *
	 * @return The current framework time in milliseconds.
	 */
	public float getTimeMs() {
		return updater.getTimeMs();
	}

	/**
	 * Gets if the framework is currently initialized.
	 *
	 * @return Is the framework is currently initialized?
	 */
	public boolean isInitialized() {
		return initialized;
	}

	/**
	 * Sets if the framework is initialized.
	 *
	 * @param initialized If the framework is initialized.
	 */
	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

	/**
	 * Gets if the framework still running.
	 *
	 * @return Is the framework still running?
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * Requests the implementation-loop to stop and the implementation to exit.
	 *
	 * @param error If a error screen will be created.
	 */
	public void requestClose(boolean error) {
		this.running = false;

		// A statement in case it was already true.
		if (error) {
			this.error = true;
		}
	}

	/**
	 * Gets the current FPS limit.
	 *
	 * @return The current FPS limit.
	 */
	public int getFpsLimit() {
		return this.fpsLimit;
	}

	/**
	 * Sets a limit to the fps, (-1 disabled limits).
	 *
	 * @param fpsLimit The FPS limit.
	 */
	public void setFpsLimit(int fpsLimit) {
		this.fpsLimit = fpsLimit;
		this.updater.setFpsLimit(fpsLimit);
	}

	public static Framework get() {
		return INSTANCE;
	}
}
