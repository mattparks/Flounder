package com.flounder.framework.updater;

import com.flounder.framework.*;
import com.flounder.logger.*;
import com.flounder.maths.*;
import com.flounder.maths.Timer;
import com.flounder.platform.FlounderPlatform;

import java.util.*;

/**
 * The default updater for the framework.
 */
public class UpdaterDefault implements IUpdater {
	private TimingReference timing;
	private double startTime;

	private float timeOffset;
	private Delta deltaUpdate;
	private Delta deltaRender;
	private Timer timerUpdate;
	private Timer timerRender;

	/**
	 * Creates a new updater.
	 */
	public UpdaterDefault() {
		// Creates variables to be used for timing updates and renders.
		this.timeOffset = 0.0f;
		this.deltaUpdate = new Delta();
		this.deltaRender = new Delta();
		this.timerUpdate = new Timer(1.0 / 60.0);
		this.timerRender = new Timer(1.0 / 60.0);
	}

	@Override
	public void run() {
		initialize();

		while (Framework.get().isRunning()) {
			if (Framework.get().isInitialized()) {
				update();
			}
		}
	}

	private void initialize() {
		if (Framework.get().isInitialized()) {
			return;
		}

		this.timing = FlounderPlatform.get().getTiming();
		// Sets basic updater info.
		this.startTime = timing.getTime();

		// Initializes all modules.
		Framework.get().runHandlers(Handler.FLAG_INIT);

		// Logs initialize times.
		FlounderLogger.get().init("Flounder Initialize & Load Time: " + FlounderLogger.ANSI_RED + (getTimeSec() - startTime) + FlounderLogger.ANSI_RESET + " seconds!");

		// Sets the framework as initialized.
		Framework.get().setInitialized(true);
	}

	private void update() {
		// Updates the module when needed always.
		Framework.get().runHandlers(Handler.FLAG_UPDATE_ALWAYS);

		// Updates when needed.
		if (timerUpdate.isPassedTime()) {
			// Resets the timer.
			timerUpdate.resetStartTime();

			// Updates the frameworks delta.
			deltaUpdate.update();

			// Updates the modules when needed before the entrance.
			Framework.get().runHandlers(Handler.FLAG_UPDATE_PRE);

			// Updates the modules when needed after the entrance.
			Framework.get().runHandlers(Handler.FLAG_UPDATE_POST);
		}

		// Renders when needed.
		if ((timerRender.isPassedTime() || Framework.get().getFpsLimit() == -1 || Framework.get().getFpsLimit() > 1000.0f) && Maths.almostEqual(timerUpdate.getInterval(), deltaUpdate.getDelta(), 10.0)) {
			// Resets the timer.
			timerRender.resetStartTime();

			// Updates the render delta, and render time extension.
			deltaRender.update();

			// Updates the module when needed after the rendering.
			Framework.get().runHandlers(Handler.FLAG_RENDER);
		}
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
		if (!Framework.get().isInitialized()) {
			return;
		}

		FlounderLogger.get().warning("Disposing framework!"); // A new Framework object must be recreated if resetting the framework!

		Collections.reverse(Framework.get().getModules());
		Framework.get().runHandlers(Handler.FLAG_DISPOSE);

		Framework.get().getModules().clear();
		Framework.get().setInitialized(false);
	}

	@Override
	public float getTimeOffset() {
		return timeOffset;
	}

	@Override
	public void setTimeOffset(float timeOffset) {
		this.timeOffset = timeOffset;
	}

	@Override
	public float getDelta() {
		return (float) deltaUpdate.getDelta();
	}

	@Override
	public float getDeltaRender() {
		return (float) deltaRender.getDelta();
	}

	@Override
	public void setFpsLimit(float fpsLimit) {
		this.timerRender.setInterval(Math.abs(1.0f / fpsLimit));
	}

	@Override
	public float getTimeSec() {
		double time;

		if (timing != null) {
			time = timing.getTime();
		} else {
			time = System.nanoTime() * 1e-9;
		}

		return (float) (time - startTime) + timeOffset;
	}

	@Override
	public float getTimeMs() {
		return getTimeSec() * 1000.0f;
	}
}
