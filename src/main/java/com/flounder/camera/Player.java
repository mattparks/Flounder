package com.flounder.camera;

import com.flounder.framework.*;
import com.flounder.maths.vectors.*;

/**
 * This interface is used to move and add extra rotation to a camera.
 */
public abstract class Player extends Extension {
	/**
	 * Creates a new standards.
	 *
	 * @param requires The classes that are extra requirements for this implementation.
	 */
	public Player(Class... requires) {
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
