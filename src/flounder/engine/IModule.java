package flounder.engine;

public class IModule {
	private static IGame game;
	private static ICamera camera;
	private static IRendererMaster masterRenderer;

	/**
	 * @param game The game to be run with the engine.
	 * @param camera The main camera to use.
	 * @param masterRenderer The master renderer to render with.
	 */
	public IModule(IGame game, ICamera camera, IRendererMaster masterRenderer) {
		IModule.game = game;
		IModule.camera = camera;
		IModule.masterRenderer = masterRenderer;
	}

	protected void init() {
		masterRenderer.init();
		camera.init();
		game.init();
	}

	protected void update() {
		game.update();
		camera.moveCamera(game.getFocusPosition(), game.getFocusRotation(), game.isGamePaused());
	}

	protected void render() {
		masterRenderer.render();
	}

	protected IGame getGame() {
		return game;
	}

	protected ICamera getCamera() {
		return camera;
	}

	protected IRendererMaster getRendererMaster() {
		return masterRenderer;
	}

	/**
	 * Deals with closing down the module.
	 */
	protected void dispose() {
		game.dispose();
		masterRenderer.dispose();
	}
}
