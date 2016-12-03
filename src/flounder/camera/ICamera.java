package flounder.camera;

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
	 * @param player The movement and rotation controller to read from.
	 */
	void update(IPlayer player);

	/**
	 * Gets the view frustum created by the current camera position and rotation.
	 *
	 * @return The view frustum created by the current camera position and rotation.
	 */
	Frustum getViewFrustum();

	/**
	 * Gets the view matrix created by the current camera position and rotation.
	 *
	 * @return The view matrix created by the current camera position and rotation.
	 */
	Matrix4f getViewMatrix();

	/**
	 * Gets the projection matrix used in the current scene renderObjects.
	 *
	 * @return The projection matrix used in the current scene renderObjects.
	 */
	Matrix4f getProjectionMatrix();

	/**
	 * Prepares the camera for the reflection renderObjects pass.
	 *
	 * @param waterHeight The height of the water to be reflected on.
	 */
	void reflect(float waterHeight);

	/**
	 * Gets the cameras 3D position in the world.
	 *
	 * @return The cameras 3D position in the world.
	 */
	@Override
	Vector3f getPosition();

	/**
	 * Gets the cameras 3D rotation in the world, where x=pitch, y=yaw, z=roll.
	 *
	 * @return The cameras 3D rotation in the world, where x=pitch, y=yaw, z=roll.
	 */
	Vector3f getRotation();

	/**
	 * Sets the rotation of the camera, where x=pitch, y=yaw, z=roll.
	 *
	 * @param rotation The cameras new rotation.
	 */
	void setRotation(Vector3f rotation);
}

