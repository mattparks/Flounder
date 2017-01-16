package flounder.entities.components;

import flounder.entities.*;
import flounder.maths.vectors.*;

/**
 * Defines a function to be called when moving entities.
 */
public interface IComponentMove {
	/**
	 * A function that is called when a entity needs to be moved.
	 *
	 * @param entity The entity being moved.
	 * @param moveAmount The amount being moved.
	 * @param rotateAmount The amount being rotated.
	 */
	void move(Entity entity, Vector3f moveAmount, Vector3f rotateAmount);
}
