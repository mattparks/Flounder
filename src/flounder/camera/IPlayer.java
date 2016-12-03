package flounder.camera;

import flounder.maths.vectors.*;

/**
 * This interface is used to move and add extra rotation to a camera.
 */
public interface IPlayer {
	/**
	 * Used to initialise the camera.
	 */
	void init();

	/**
	 * Checks inputs and carries out player movement. Should be called every frame.
	 */
	void update();

	/**
	 * Gets the players 3D position in the world.
	 *
	 * @return The players 3D position in the world.
	 */
	Vector3f getPosition();

	/**
	 * Gets the players 3D rotation in the world, where x=pitch, y=yaw, z=roll.
	 *
	 * @return The players 3D rotation in the world, where x=pitch, y=yaw, z=roll.
	 */
	Vector3f getRotation();
}
