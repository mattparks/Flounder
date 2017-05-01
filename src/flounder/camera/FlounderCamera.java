package flounder.camera;

import flounder.devices.*;
import flounder.entities.*;
import flounder.framework.*;
import flounder.profiling.*;

/**
 * A module used for managing cameras in 2D and 3D worlds.
 */
public class FlounderCamera extends Module {
	private Player player;
	private Camera camera;

	/**
	 * Creates a new camera manager.
	 */
	public FlounderCamera() {
		super(FlounderJoysticks.class, FlounderKeyboard.class, FlounderMouse.class, FlounderEntities.class);
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
		this.player = null;
		this.camera = null;
	}

	@Handler.Function(Handler.FLAG_UPDATE_PRE)
	public void update() {
		// Gets a new player and camera, if available.
		Player newPlayer = (Player) getExtension(player, Player.class, true);
		Camera newCamera = (Camera) getExtension(camera, Camera.class, true);

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

	@Handler.Function(Handler.FLAG_PROFILE)
	public void profile() {
		FlounderProfiler.get().add(getTab(), "Camera Selected", camera == null ? "NULL" : camera.getClass());
		FlounderProfiler.get().add(getTab(), "Player Selected", player == null ? "NULL" : player.getClass());
	}

	/**
	 * Gets the current player extension.
	 *
	 * @return The current player.
	 */
	public Player getPlayer() {
		return this.player;
	}

	/**
	 * Gets the current camera extension.
	 *
	 * @return The current camera.
	 */
	public Camera getCamera() {
		return this.camera;
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
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

	@Module.Instance
	public static FlounderCamera get() {
		return (FlounderCamera) Framework.getInstance(FlounderCamera.class);
	}

	@Module.TabName
	public static String getTab() {
		return "Camera";
	}
}
