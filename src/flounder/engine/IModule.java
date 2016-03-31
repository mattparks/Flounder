package flounder.engine;

public class IModule {
	private static IGame m_game;
	private static ICamera m_camera;
	private static IRendererMaster m_masterRenderer;

	/**
	 * @param game The game to be run with the engine.
	 * @param camera The main camera to use.
	 * @param masterRenderer The master renderer to render with.
	 */
	public IModule(final IGame game, final ICamera camera, final IRendererMaster masterRenderer) {
		m_game = game;
		m_camera = camera;
		m_masterRenderer = masterRenderer;
	}

	protected void init() {
		m_masterRenderer.init();
		m_game.init();
	}

	protected void update() {
		m_game.update();
		m_camera.moveCamera(m_game.getFocusPosition(), m_game.getFocusRotation(), m_game.isGamePaused());
	}

	protected void render() {
		m_masterRenderer.render();
	}

	protected IGame getGame() {
		return m_game;
	}

	protected ICamera getCamera() {
		return m_camera;
	}

	protected IRendererMaster getRendererMaster() {
		return m_masterRenderer;
	}

	protected void dispose() {
		m_game.dispose();
		m_masterRenderer.dispose();
	}
}
