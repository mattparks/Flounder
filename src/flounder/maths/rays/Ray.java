package flounder.maths.rays;

import flounder.engine.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;

/**
 * Holds a 3 dimensional ray.
 */
public class Ray {
	private static final int RECURSION_COUNT = 5;
	private static final float RAY_RANGE = 120.0f;
	private static final float RAY_SECTION = 2.0f;

	private boolean useMouse;
	private Vector2f rayStart;

	private Vector3f cameraPosition;
	private Vector3f currentRay;

	/**
	 * Creates a new 3D ray.
	 *
	 * @param useMouse If the ray will use the mouse coords or to start from rayStart.
	 * @param rayStart If useMouse is false then this will be used as the rays start.
	 */
	public Ray(boolean useMouse, Vector2f rayStart) {
		this.useMouse = useMouse;
		this.rayStart = rayStart;

		this.cameraPosition = new Vector3f();
		this.currentRay = new Vector3f();
	}

	public void update(Vector3f currentPosition) {
		this.cameraPosition.set(currentPosition);
		currentRay = calculateMouseRay();
	}

	private Vector3f calculateMouseRay() {
		Vector2f normalizedCoords;

		if (!useMouse && rayStart != null) {
			float mouseX = FlounderEngine.getGuis().getSelector().getCursorX();
			float mouseY = FlounderEngine.getGuis().getSelector().getCursorY();
			normalizedCoords = getNormalisedDeviceCoordinates(mouseX, mouseY);
		} else {
			normalizedCoords = new Vector2f(0.0f, 0.0f);
		}

		Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1.0f, 1.0f);
		Vector4f eyeCoords = toEyeCoords(clipCoords);
		Vector3f worldRay = toWorldCoords(eyeCoords);
		return worldRay;
	}

	private Vector2f getNormalisedDeviceCoordinates(float mouseX, float mouseY) {
		float x = (2.0f * mouseX) / FlounderEngine.getDevices().getDisplay().getWidth() - 1.0f;
		float y = (2.0f * mouseY) / FlounderEngine.getDevices().getDisplay().getHeight() - 1.0f;
		return new Vector2f(x, y);
	}

	private Vector4f toEyeCoords(Vector4f clipCoords) {
		Matrix4f invertedProjection = Matrix4f.invert(FlounderEngine.getProjectionMatrix(), null);
		Vector4f eyeCoords = Matrix4f.transform(invertedProjection, clipCoords, null);
		return new Vector4f(eyeCoords.x, eyeCoords.y, -1.0f, 0.0f);
	}

	private Vector3f toWorldCoords(Vector4f eyeCoords) {
		Matrix4f invertedView = Matrix4f.invert(FlounderEngine.getCamera().getViewMatrix(), null);
		Vector4f rayWorld = Matrix4f.transform(invertedView, eyeCoords, null);
		Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
		mouseRay.normalize();
		return mouseRay;
	}

	public Vector3f getCameraPosition() {
		return cameraPosition;
	}

	public Vector3f getCurrentRay() {
		return currentRay;
	}

	public Vector3f getPointOnRay(float distance) {
		Vector3f scaledRay = new Vector3f(currentRay.x * distance, currentRay.y * distance, currentRay.z * distance); // new Vector3f(currentRay).scale(distance);
		return Vector3f.add(cameraPosition, scaledRay, null);
	}
}
