package flounder.camera;

import flounder.framework.*;
import flounder.logger.*;
import flounder.maths.vectors.*;
import flounder.profiling.*;

import java.util.*;

public class FlounderCamera extends IModule {
	private static final FlounderCamera instance = new FlounderCamera();

	private ICamera camera;
	private Vector3f focusPosition;
	private Vector3f focusRotation;
	private boolean gamePaused;

	public FlounderCamera() {
		super(ModuleUpdate.RENDER, FlounderLogger.class, FlounderProfiler.class);
		this.camera = null;
		this.focusPosition = new Vector3f();
		this.focusRotation = new Vector3f();
		this.gamePaused = false;
	}

	@Override
	public void init() {
		if (camera != null) {
			camera.init();
			((IExtension) camera).setInitialized(true);
		}
	}

	@Override
	public void run() {
		List<IExtension> cameraExtensions = null;

		for (IExtension extension : FlounderFramework.getExtensions()) {
			if (extension instanceof ICamera) {
				cameraExtensions = new ArrayList<>();
				cameraExtensions.add(extension);
			}
		}

		if (cameraExtensions != null && !cameraExtensions.isEmpty()) {
			for (IExtension extension : cameraExtensions) {
				ICamera newCamera = (ICamera) extension;

				if (newCamera.isActive() && !newCamera.equals(camera)) {
					if (camera != null) {
						((IExtension) camera).setInitialized(false);
					}

					camera = newCamera;

					if (!extension.isInitialized()) {
						camera.init();
						((IExtension) camera).setInitialized(true);
					}

					break;
				}
			}
		}

		if (camera != null) {
			camera.update(focusPosition, focusRotation, gamePaused);
		}
	}

	@Override
	public void profile() {
		FlounderProfiler.add("Camera", "Selected", camera == null ? "NULL" : camera.getClass());
	}

	public static ICamera getCamera() {
		return instance.camera;
	}

	public static Vector3f getFocusPosition() {
		return instance.focusPosition;
	}

	public static void setFocusPosition(Vector3f focusPosition) {
		instance.focusPosition.set(focusPosition);
	}

	public static Vector3f getFocusRotation() {
		return instance.focusRotation;
	}

	public static void setFocusRotation(Vector3f focusRotation) {
		instance.focusRotation.set(focusRotation);
	}

	public static boolean isGamePaused() {
		return instance.gamePaused;
	}

	public static void setGamePaused(boolean gamePaused) {
		instance.gamePaused = gamePaused;
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

		gamePaused = true;
	}
}
