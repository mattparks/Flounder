package flounder.entities.components;

import flounder.physics.*;

/**
 * Defines a function to be called when looking for entity boundings.
 */
public interface IComponentCollider {
	/**
	 * A method that can be implemented to a component that adda boundings to the entity.
	 *
	 * @return The bounding, null if not adding one.
	 */
	Collider getBounding();
}
