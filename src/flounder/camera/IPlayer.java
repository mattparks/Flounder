package flounder.camera;

import flounder.framework.*;
import flounder.maths.vectors.*;

/**
 * This interface is used to move and add extra rotation to a camera.
 */
public abstract class IPlayer extends IExtension {
	/**
	 * Creates a new standard.
	 *
	 * @param requires The classes that are extra requirements for this implementation.
	 */
	public IPlayer(Class... requires) {
		super(FlounderCamera.class, requires);
	}

	/**
	 * Used to initialise the player.
	 */
	public abstract void init();

	/**
	 * Checks inputs and carries out player movement. Should be called every frame.
	 */
	public abstract void update();

	/**
	 * Gets the players 3D position in the world.
	 *
	 * @return The players 3D position in the world.
	 */
	public abstract Vector3f getPosition();

	/**
	 * Gets the players 3D rotation in the world, where x=pitch, y=yaw, z=roll.
	 *
	 * @return The players 3D rotation in the world, where x=pitch, y=yaw, z=roll.
	 */
	public abstract Vector3f getRotation();

	@Override
	public abstract boolean isActive();
}
