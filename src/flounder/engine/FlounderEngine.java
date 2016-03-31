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
public class FlounderEngine {
	private static boolean m_initialized;
	private static IModule m_module;

	private static float m_targetFPS;
	private static float m_currentFrameTime;
	private static float m_lastFrameTime;
	private static long m_timerStart;
	private static float m_frames;
	private static float m_updates;
	private static float m_delta;
	private static float m_time;

	/**
	 * Carries out any necessary initializations of the engine.
	 *
	 * @param module The module for the engine to run off of.
	 * @param displayWidth The window width in pixels.
	 * @param displayHeight The window height in pixels.
	 * @param displayTitle The window title.
	 * @param targetFPS The engines target frames per second.
	 * @param displayVSync If the window will use vSync..
	 * @param antialiasing If OpenGL will use altialiasing.
	 * @param displayFullscreen If the window will start fullscreen.
	 */
	public static void init(final IModule module, final int displayWidth, final int displayHeight, final String displayTitle, final float targetFPS, final boolean displayVSync, final boolean antialiasing, final boolean displayFullscreen) {
		if (!m_initialized) {
			m_targetFPS = targetFPS;
			m_currentFrameTime = 0.0f;
			m_lastFrameTime = 0.0f;
			m_timerStart = System.currentTimeMillis() + 1000;
			m_frames = 0.0f;
			m_updates = 0.0f;
			m_delta = 0.0f;
			m_time = 0.0f;

			ManagerDevices.init(displayWidth, displayHeight, displayTitle, displayVSync, antialiasing, displayFullscreen);
			(m_module = module).init();
			EngineProfiler.init();
			m_initialized = true;
		}
	}

	public static void run() {
		while (ManagerDevices.getDisplay().isOpen()) {
			boolean render = false;

			{
				render = true;
				update();

				// Updates static delta and times.
				m_currentFrameTime = ManagerDevices.getDisplay().getTime() / 1000.0f;
				m_delta = m_currentFrameTime - m_lastFrameTime;
				m_lastFrameTime = m_currentFrameTime;

				// Prints out current engine update and frame stats.
				if (System.currentTimeMillis() - m_timerStart > 1000) {
					System.out.println(m_updates + "ups, " + m_frames + "fps.");
					m_timerStart += 1000;
					m_updates = 0;
					m_frames = 0;
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

	/**
	 * Updates many engine systems before every frame.
	 */
	public static void update() {
		ManagerDevices.preRender(m_delta);
		m_module.update();
		GuiManager.updateGuis();
		m_updates++;
	}

	/**
	 * Renders the engines master renderer and carries out OpenGL request calls.
	 */
	public static void render() {
		m_module.render();
		ManagerDevices.postRender();
		GlRequestProcessor.dealWithTopRequests();
		m_frames++;
	}

	/**
	 * @return The engines camera implementation.
	 */
	public static ICamera getCamera() {
		return m_module.getCamera();
	}

	/**
	 * @return The projection matrix used in the current scene renderObjects.
	 */
	public static Matrix4f getProjectionMatrix() {
		return m_module.getRendererMaster().getProjectionMatrix();
	}

	public static void setTargetFPS(final float targetFPS) {
		m_targetFPS = targetFPS;
	}

	public static float getFPS() {
		return m_frames;
	}

	public static float getUPS() {
		return m_updates;
	}

	public static float getDelta() {
		return m_delta;
	}

	public static float getTime() {
		return m_time;
	}

	/**
	 * Deals with closing down the engine and all necessary systems.
	 */
	public static void dispose() {
		if (m_initialized) {
			Loader.dispose();
			RequestProcessor.dispose();
			GlRequestProcessor.completeAllRequests();
			TextureManager.dispose();

			m_module.dispose();
			EngineProfiler.dispose();
			ManagerDevices.dispose();
			m_initialized = false;
		}
	}
}
