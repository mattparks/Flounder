package flounder.camera;

import flounder.devices.*;
import flounder.entities.*;
import flounder.framework.*;
import flounder.profiling.*;

/**
 * A module used for managing cameras in 2D and 3D worlds.
 */
public class FlounderCamera extends Module {
	private static final FlounderCamera INSTANCE = new FlounderCamera();
	public static final String PROFILE_TAB_NAME = "Camera";

	private Player player;
	private Camera camera;

	/**
	 * Creates a new camera manager.
	 */
	public FlounderCamera() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME, FlounderJoysticks.class, FlounderKeyboard.class, FlounderMouse.class, FlounderEntities.class);
	}

	@Override
	public void init() {
		this.player = null;
		this.camera = null;
	}

	@Override
	public void update() {
		// Gets a new player and camera, if available.
		Player newPlayer = (Player) getExtensionMatch(player, Player.class, true);
		Camera newCamera = (Camera) getExtensionMatch(camera, Camera.class, true);

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
		FlounderProfiler.add(PROFILE_TAB_NAME, "Camera Selected", camera == null ? "NULL" : camera.getClass());
		FlounderProfiler.add(PROFILE_TAB_NAME, "Player Selected", player == null ? "NULL" : player.getClass());
	}

	/**
	 * Gets the current player extension.
	 *
	 * @return The current player.
	 */
	public static Player getPlayer() {
		return INSTANCE.player;
	}

	/**
	 * Gets the current camera extension.
	 *
	 * @return The current camera.
	 */
	public static Camera getCamera() {
		return INSTANCE.camera;
	}

	@Override
	public Module getInstance() {
		return INSTANCE;
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
