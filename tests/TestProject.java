import flounder.devices.FlounderDisplay;
import flounder.framework.*;
import flounder.framework.updater.*;
import flounder.logger.*;
import flounder.maths.*;
import flounder.standards.*;
import org.lwjgl.glfw.GLFW;

/**
 * The class that contains the main method.
 */
public class TestProject extends Framework {
	public static void main(String[] args) {
		// Creates and runs a new framework object.
		new TestProject().run();

		// After close, exits the programs.
		System.exit(0);
	}

	public TestProject() {
		super(
				"test", new UpdaterDefault(GLFW::glfwGetTime), -1,
				new Extension[]{new TestInterface()}
		);
	}

	/**
	 * The programs interface, this one is used for a simple close countdown.
	 */
	public static class TestInterface extends Standard {
		private static final int INTERVAL_CLOSE = 10;

		private Timer timer;
		private int i;

		public TestInterface() {
			super(FlounderLogger.class);
		}

		@Override
		public void init() {
			FlounderLogger.get().log("TestInterface initialized!");

			FlounderDisplay.get().setHidden(false);

			this.timer = new Timer(1.0);
			this.i = 0;
		}

		@Override
		public void update() {
			// Called in the update pre loop. Framework update order: Always, /Pre/, Post, Render.

			// A simple close countdown.
			if (timer.isPassedTime()) {
				i++;

				if (i == INTERVAL_CLOSE) {
					FlounderLogger.get().log("TestInterface requesting close!");
					Framework.get().requestClose(false);
				} else {
					FlounderLogger.get().log("TestInterface closing after: " + (INTERVAL_CLOSE - i) + " seconds!");
				}

				timer.resetStartTime();
			}
		}

		@Override
		public void profile() {
			// Called after every update, if the profiler is open.
		}

		@Override
		public void dispose() {
			FlounderLogger.get().log("TestInterface disposed!");
		}

		@Override
		public boolean isActive() {
			return true;
		}
	}
}