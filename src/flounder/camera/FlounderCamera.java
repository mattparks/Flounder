package flounder.camera;

import flounder.framework.*;
import flounder.logger.*;
import flounder.profiling.*;

/**
 * A module used for managing cameras in 2D and 3D worlds.
 */
public class FlounderCamera extends IModule {
	private static final FlounderCamera instance = new FlounderCamera();

	private IPlayer player;
	private ICamera camera;

	/**
	 * Creates a new camera manager.
	 */
	public FlounderCamera() {
		super(ModuleUpdate.UPDATE_POST, FlounderLogger.class, FlounderProfiler.class);
	}

	@Override
	public void init() {
		this.player = null;
		this.camera = null;
	}

	@Override
	public void update() {
		IPlayer newPlayer = (IPlayer) FlounderModules.getExtensionMatch(getInstance(), player, true);
		ICamera newCamera = (ICamera) FlounderModules.getExtensionMatch(getInstance(), camera, true);

		if (newPlayer != null) {
			if (player != null) {
				player.setInitialized(false);
			}

			player = newPlayer;
		}

		if (newCamera != null) {
			if (camera != null) {
				camera.setInitialized(false);
			}

			camera = newCamera;
		}

		if (player != null) {
			if (!player.isInitialized()) {
				player.init();
				player.setInitialized(true);
			}

			player.update();
		}

		if (camera != null) {
			if (!camera.isInitialized()) {
				camera.init();
				camera.setInitialized(true);
			}

			camera.update(player);
		}
	}

	@Override
	public void profile() {
		FlounderProfiler.add("Camera", "Camera Selected", camera == null ? "NULL" : camera.getClass());
		FlounderProfiler.add("Camera", "Player Selected", player == null ? "NULL" : player.getClass());
	}

	public static IPlayer getPlayer() {
		return instance.player;
	}

	public static ICamera getCamera() {
		return instance.camera;
	}

	@Override
	public IModule getInstance() {
		return instance;
	}

	@Override
	public void dispose() {
		if (camera != null) {
			camera.setInitialized(false);
		}
	}
}
