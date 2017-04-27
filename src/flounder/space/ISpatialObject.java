package flounder.space;

import flounder.physics.*;

/**
 * Represents an object that has some notion of space, and can be stored in a {@link ISpatialStructure}.
 */
public interface ISpatialObject {
	/**
	 * @return Returns a shape fully enclosing the object.
	 */
	Collider getCollider();
}
