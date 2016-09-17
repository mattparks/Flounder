package flounder.physics;

import flounder.engine.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;

/**
 * Holds a 3 dimensional ray.
 */
public class Ray {
	private boolean useMouse;
	private Vector2f screenStart;

	private Vector3f origin;
	private Vector3f currentRay;

	private Vector2f normalizedCoords;
	private Vector4f clipCoords;
	private Vector4f eyeCoords;

	private Matrix4f invertedProjection;
	private Matrix4f invertedView;
	private Vector4f rayWorld;

	/**
	 * Creates a new 3D ray.
	 *
	 * @param useMouse If the ray will use the mouse coords or to start from screenStart.
	 * @param screenStart If useMouse is false then this will be used as the rays start.
	 */
	public Ray(boolean useMouse, Vector2f screenStart) {
		this.useMouse = useMouse;
		this.screenStart = screenStart;

		this.origin = new Vector3f();
		this.currentRay = new Vector3f();

		this.normalizedCoords = new Vector2f();
		this.clipCoords = new Vector4f();
		this.eyeCoords = new Vector4f();

		this.invertedProjection = new Matrix4f();
		this.invertedView = new Matrix4f();
		this.rayWorld = new Vector4f();
	}

	/**
	 * Updates the ray to a new position.
	 *
	 * @param currentPosition The new position.
	 */
	public void update(Vector3f currentPosition) {
		origin.set(currentPosition);

		if (useMouse) {
			float mouseX = FlounderEngine.getDevices().getMouse().getPositionX();
			float mouseY = FlounderEngine.getDevices().getMouse().getPositionY();
			updateNormalisedDeviceCoordinates(mouseX, mouseY);
		} else {
			if (screenStart != null) {
				normalizedCoords.set(screenStart);
			} else {
				normalizedCoords.set(0.0f, 0.0f);
			}
		}

		clipCoords.set(normalizedCoords.x, normalizedCoords.y, -1.0f, 1.0f);
		updateEyeCoords(clipCoords);
		updateWorldCoords(eyeCoords);
	}

	private void updateNormalisedDeviceCoordinates(float mouseX, float mouseY) {
		float x = (2.0f * mouseX) / FlounderEngine.getDevices().getDisplay().getWidth() - 1.0f;
		float y = (2.0f * mouseY) / FlounderEngine.getDevices().getDisplay().getHeight() - 1.0f;
		normalizedCoords.set(x, y);
	}

	private void updateEyeCoords(Vector4f clipCoords) {
		invertedProjection = Matrix4f.invert(FlounderEngine.getProjectionMatrix(), invertedProjection);
		Matrix4f.transform(invertedProjection, clipCoords, eyeCoords);
		eyeCoords.set(eyeCoords.x, eyeCoords.y, -1.0f, 0.0f);
	}

	private void updateWorldCoords(Vector4f eyeCoords) {
		Matrix4f.invert(FlounderEngine.getCamera().getViewMatrix(), invertedView);
		Matrix4f.transform(invertedView, eyeCoords, rayWorld);
		currentRay.set(rayWorld.x, rayWorld.y, rayWorld.z);
	}

	/**
	 * Gets the rays origin.
	 *
	 * @return The rays origin.
	 */
	public Vector3f getOrigin() {
		return origin;
	}

	/**
	 * Gets the current ray.
	 *
	 * @return The current ray.
	 */
	public Vector3f getCurrentRay() {
		return currentRay;
	}

	/**
	 * Gets a point on the ray.
	 *
	 * @param distance Distance down the ray to sample.
	 * @param destination The destination vector, if null one will be created.
	 *
	 * @return Returns the destination vector.
	 */
	public Vector3f getPointOnRay(float distance, Vector3f destination) {
		if (destination == null) {
			destination = new Vector3f();
		}

		return Vector3f.add(origin, destination.set(currentRay).scale(distance), destination);
	}

	/**
	 * Converts a position from world space to screen space.
	 *
	 * @param position The position to convert.
	 * @param destination The destination point. X and Y being screen space coords and Z being the distance to the camera.
	 *
	 * @return Returns the destination vector.
	 */
	public Vector3f convertToScreenSpace(Vector3f position, Vector3f destination) {
		if (destination == null) {
			destination = new Vector3f();
		}

		Vector4f coords = new Vector4f(position.x, position.y, position.z, 1.0f);
		Matrix4f.transform(FlounderEngine.getCamera().getViewMatrix(), coords, coords);
		Matrix4f.transform(FlounderEngine.getProjectionMatrix(), coords, coords);

		if (coords.w < 0.0f) {
			return null;
		}

		return destination.set((coords.x / coords.w + 1.0f) / 2.0f, 1.0f - (coords.y / coords.w + 1.0f) / 2.0f, coords.z);
	}

	@Override
	public String toString() {
		return "Ray{ origin=" + origin + ", currentRay=" + currentRay + " }";
	}
}
