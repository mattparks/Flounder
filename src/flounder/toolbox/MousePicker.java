package flounder.toolbox;

import flounder.devices.*;
import flounder.engine.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;

public class MousePicker {
	private static final int RECURSION_COUNT = 5;
	private static final float RAY_RANGE = 120.0f;
	private static final float RAY_SECTION = 2.0f;

	private Vector3f currentRay = new Vector3f();
	private Matrix4f viewMatrix;
	private IPointSearch pointSearch;
	private ICamera camera;
	private Vector3f currentTerrainPoint;
	private boolean pickCenterScreen;
	private boolean rayUpToDate = false;
	private boolean terrainPointUpToDate = false;

	public MousePicker(final ICamera camera, final IPointSearch pointSearch, final boolean pickCenter) {
		this.camera = camera;
		this.pointSearch = pointSearch;
		viewMatrix = camera.getViewMatrix();
		pickCenterScreen = pickCenter;
	}

	public Vector3f getCurrentTerrainPoint() {
		if (!terrainPointUpToDate) {
			updateTerrainPoint();
		}

		return currentTerrainPoint;
	}

	public Vector3f getCurrentRay() {
		if (!rayUpToDate) {
			updateMouseRay();
		}

		return currentRay;
	}

	public void update() {
		viewMatrix = camera.getViewMatrix();
		rayUpToDate = false;
		terrainPointUpToDate = false;
	}

	private void updateTerrainPoint() {
		Vector3f ray = getCurrentRay();

		if (intersectionInRange(0.0f, 120.0f, ray)) {
			float section = getSectionID(ray);
			currentTerrainPoint = binarySearch(0, section * 2.0f, (section + 1.0f) * 2.0f, ray);
		} else {
			currentTerrainPoint = null;
		}

		terrainPointUpToDate = true;
	}

	private void updateMouseRay() {
		Vector2f normalizedCoords;

		if (pickCenterScreen) {
			normalizedCoords = new Vector2f(0.0f, 0.0f);
		} else {
			normalizedCoords = getNormalisedDeviceCoordinates((float) ManagerDevices.getMouse().getPositionX(), (float) ManagerDevices.getMouse().getPositionY());
		}

		currentRay = toWorldCoords(toEyeCoords(new Vector4f(normalizedCoords.x, normalizedCoords.y, -1.0f, 1.0f)));
		rayUpToDate = true;
	}

	private Vector3f toWorldCoords(final Vector4f eyeCoords) {
		Matrix4f invertedView = Matrix4f.invert(viewMatrix, null);
		Vector4f rayWorld = Matrix4f.transform(invertedView, eyeCoords, null);
		Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
		mouseRay.normalize();
		return mouseRay;
	}

	private Vector4f toEyeCoords(final Vector4f clipCoords) {
		Matrix4f invertedProjection = Matrix4f.invert(FlounderEngine.getProjectionMatrix(), null);
		Vector4f eyeCoords = Matrix4f.transform(invertedProjection, clipCoords, null);
		return new Vector4f(eyeCoords.x, eyeCoords.y, -1.0f, 0.0f);
	}

	private Vector2f getNormalisedDeviceCoordinates(final float mouseX, final float mouseY) {
		float x = 2.0f * mouseX / ManagerDevices.getDisplay().getDisplayWidth() - 1.0f;
		float y = 2.0f * mouseY / ManagerDevices.getDisplay().getDisplayHeight() - 1.0f;
		return new Vector2f(x, y);
	}

	private int getSectionID(final Vector3f ray) {
		for (int i = 0; i < 60.0f; ++i) {
			if (intersectionInRange(i * 2.0f, (i + 1) * 2.0f, ray)) {
				return i;
			}
		}

		return 60;
	}

	private Vector3f getPointOnRay(final Vector3f ray, final float distance) {
		Vector3f camPos = camera.getPosition();
		Vector3f start = new Vector3f(camPos.x, camPos.y, camPos.z);
		Vector3f scaledRay = new Vector3f(ray.x * distance, ray.y * distance, ray.z * distance);
		return Vector3f.add(start, scaledRay, null);
	}

	private Vector3f binarySearch(final int count, final float start, final float finish, final Vector3f ray) {
		float half = start + (finish - start) / 2.0f;

		if (count >= RECURSION_COUNT) {
			Vector3f endPoint = getPointOnRay(ray, half);
			return endPoint;
		}

		if (intersectionInRange(start, half, ray)) {
			return binarySearch(count + 1, start, half, ray);
		}

		return binarySearch(count + 1, half, finish, ray);
	}

	private boolean intersectionInRange(final float start, final float finish, final Vector3f ray) {
		Vector3f startPoint = getPointOnRay(ray, start);
		Vector3f endPoint = getPointOnRay(ray, finish);
		return !isUnderGround(startPoint) && isUnderGround(endPoint);
	}

	private boolean isUnderGround(final Vector3f testPoint) {
		float height = pointSearch.getTerrainHeight(testPoint.getX(), testPoint.getZ());
		return testPoint.y < height;
	}
}
