package com.flounder.entities;

import com.flounder.maths.vectors.*;

/**
 * Defines a function to be called when moving entities.
 */
public interface IComponentMove {
	/**
	 * A function that is called when a entity needs to be moved, this is used to verify the movement.
	 * Collision detection can be done from this function.
	 *
	 * @param entity The entity being moved.
	 * @param moveAmount The amount being moved.
	 * @param rotateAmount The amount being rotated.
	 */
	void verifyMove(Entity entity, Vector3f moveAmount, Vector3f rotateAmount);
}
