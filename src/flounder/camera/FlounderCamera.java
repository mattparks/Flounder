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
		// Gets a new player and camera, if available.
		IPlayer newPlayer = (IPlayer) FlounderModules.getExtensionMatch(getInstance(), player, IPlayer.class, true);
		ICamera newCamera = (ICamera) FlounderModules.getExtensionMatch(getInstance(), camera, ICamera.class, true);

		// If there is a new player, disable the old one and start to use the new one.
		if (newPlayer != null) {
			if (player != null) {
				player.setInitialized(false);
			}

			if (!newPlayer.isInitialized()) {
				newPlayer.init();
				newPlayer.setInitialized(true);
			}

			player = newPlayer;
		}

		// If there is a new camera, disable the old one and start to use the new one.
		if (newCamera != null) {
			if (camera != null) {
				camera.setInitialized(false);
			}

			if (!newCamera.isInitialized()) {
				newCamera.init();
				newCamera.setInitialized(true);
			}

			camera = newCamera;
		}

		// Runs updates for the player.
		if (player != null) {
			player.update();
		}

		// Runs updates for the camera.
		if (camera != null) {
			camera.update(player);
		}
	}

	@Override
	public void profile() {
		FlounderProfiler.add("Camera", "Camera Selected", camera == null ? "NULL" : camera.getClass());
		FlounderProfiler.add("Camera", "Player Selected", player == null ? "NULL" : player.getClass());
	}

	/**
	 * Gets the current player extension.
	 *
	 * @return The current player.
	 */
	public static IPlayer getPlayer() {
		return instance.player;
	}

	/**
	 * Gets the current camera extension.
	 *
	 * @return The current camera.
	 */
	public static ICamera getCamera() {
		return instance.camera;
	}

	@Override
	public IModule getInstance() {
		return instance;
	}

	@Override
	public void dispose() {
		// Disposes the player with the module.
		if (player != null) {
			player.setInitialized(false);
		}

		// Disposes the camera with the module.
		if (camera != null) {
			camera.setInitialized(false);
		}
	}
}
