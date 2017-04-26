package flounder.camera;

import flounder.framework.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;
import flounder.physics.*;
import flounder.sounds.*;

/**
 * A extension used with {@link flounder.camera.FlounderCamera} to define a camera.
 * This class is used throughout the engine wherever the camera is involved, so that the engine doesn't rely at all on the camera's implementation.
 */
public abstract class Camera extends Extension implements IAudioListener {
	/**
	 * Creates a new camera.
	 *
	 * @param requires The classes that are extra requirements for this implementation.
	 */
	public Camera(Class... requires) {
		super(FlounderCamera.class, requires);
	}

	/**
	 * Used to initialise the camera.
	 */
	public abstract void init();

	/**
	 * @return The distance of the near pane of the view frustum.
	 */
	public abstract float getNearPlane();

	/**
	 * @return The distance of the view frustum's far plane.
	 */
	public abstract float getFarPlane();

	/**
	 * @return The field of view angle for the view frustum.
	 */
	public abstract float getFOV();

	/**
	 * Checks inputs and carries out smooth camera movement. Should be called every frame.
	 *
	 * @param player The movement and rotation controller to read from.
	 */
	public abstract void update(Player player);

	/**
	 * Gets the view frustum created by the current camera position and rotation.
	 *
	 * @return The view frustum created by the current camera position and rotation.
	 */
	public abstract Frustum getViewFrustum();

	/**
	 * Gets the ray that extends from the cameras position though the screen.
	 *
	 * @return The cameras ray.
	 */
	public abstract Ray getViewRay();

	/**
	 * Gets the view matrix created by the current camera position and rotation.
	 *
	 * @return The view matrix created by the current camera position and rotation.
	 */
	public abstract Matrix4f getViewMatrix();

	/**
	 * Gets the projection matrix used in the current scene renderObjects.
	 *
	 * @return The projection matrix used in the current scene renderObjects.
	 */
	public abstract Matrix4f getProjectionMatrix();

	/**
	 * Prepares the camera for the reflection renderObjects pass.
	 *
	 * @param waterHeight The height of the water to be reflected on.
	 */
	public abstract void reflect(float waterHeight);

	/**
	 * Gets the cameras 3D position in the world.
	 *
	 * @return The cameras 3D position in the world.
	 */
	@Override
	public abstract Vector3f getPosition();

	/**
	 * Gets the cameras 3D rotation in the world, where x=pitch, y=yaw, z=roll.
	 *
	 * @return The cameras 3D rotation in the world, where x=pitch, y=yaw, z=roll.
	 */
	public abstract Vector3f getRotation();

	/**
	 * Sets the rotation of the camera, where x=pitch, y=yaw, z=roll.
	 *
	 * @param rotation The cameras new rotation.
	 */
	public abstract void setRotation(Vector3f rotation);

	@Override
	public abstract boolean isActive();
}

