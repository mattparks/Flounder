package flounder.entities.components;

import flounder.physics.*;

/**
 * Defines a function to be called when looking for entity colliders.
 */
public interface IComponentCollider {
	/**
	 * A method that can be implemented to a component that adds colliders to the entity.
	 *
	 * @return The collider, null if not adding one.
	 */
	Collider getCollider();
}
