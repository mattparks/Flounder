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
	public void run() {
		IPlayer newPlayer = (IPlayer) FlounderFramework.getExtensionMatch(IPlayer.class, (IExtension) player, true);
		ICamera newCamera = (ICamera) FlounderFramework.getExtensionMatch(ICamera.class, (IExtension) camera, true);

		if (newPlayer != null) {
			if (player != null) {
				((IExtension) player).setInitialized(false);
			}

			player = newPlayer;
		}

		if (newCamera != null) {
			if (camera != null) {
				((IExtension) camera).setInitialized(false);
			}

			camera = newCamera;
		}

		if (player != null) {
			if (!((IExtension) player).isInitialized()) {
				player.init();
				((IExtension) player).setInitialized(true);
			}

			player.update();
		}

		if (camera != null) {
			if (!((IExtension) camera).isInitialized()) {
				camera.init();
				((IExtension) camera).setInitialized(true);
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
			((IExtension) camera).setInitialized(false);
		}
	}
}
