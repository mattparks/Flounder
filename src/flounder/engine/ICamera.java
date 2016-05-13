package flounder.engine;

import flounder.maths.matrices.*;
import flounder.maths.vectors.*;
import flounder.sounds.*;
import flounder.space.*;

/**
 * This interface is used throughout the engine wherever the camera is involved, so that the engine doesn't rely at all on the camera's implementation.
 */
public interface ICamera extends IAudioListener {
	/**
	 * Used to initialise the camera.
	 */
	void init();

	/**
	 * @return The distance of the near pane of the view frustum.
	 */
	float getNearPlane();

	/**
	 * @return The distance of the view frustum's far plane.
	 */
	float getFarPlane();

	/**
	 * @return The field of view angle for the view frustum.
	 */
	float getFOV();

	/**
	 * Checks inputs and carries out smooth camera movement. Should be called every frame.
	 *
	 * @param focusPosition The position of the object the camera focuses on.
	 * @param focusRotation The rotation of the object the camera focuses on.
	 * @param gamePaused Is the game currently paused? Used to stop inputs to camera in menus.
	 */
	void moveCamera(final Vector3f focusPosition, final Vector3f focusRotation, final boolean gamePaused);

	/**
	 * @return The view matrix created by the current camera position and rotation.
	 */
	Matrix4f getViewMatrix();

	/**
	 * @return The view frustum created by the current camera position and rotation.
	 */
	Frustum getViewFrustum();

	/**
	 * Calculates the view matrix for the reflection pass, given the height of the water plane.
	 *
	 * @param planeHeight The height of the water.
	 *
	 * @return The view matrix to be used for the reflection renderObjects pass.
	 */
	Matrix4f getReflectionViewMatrix(final float planeHeight);

	/**
	 * Prepares the camera for the reflection renderObjects pass.
	 *
	 * @param waterHeight The height of the water to be reflected on.
	 */
	void reflect(final float waterHeight);

	/**
	 * @return The camera's 3D position in the world.
	 */
	@Override
	Vector3f getPosition();

	/**
	 * @return The camera's pitch (x rotation).
	 */
	float getPitch();

	/**
	 * @return The camera's yaw (y rotation).
	 */
	float getYaw();

	/**
	 * @return The camera's roll (z rotation).
	 */
	float getRoll();

	/**
	 * @return The cameras aim distance at the terrain.
	 */
	float getAimDistance();
}

