package flounder.space;

import flounder.physics.*;

/**
 * Represents an object that has some notion of flounder.space, and can be stored in a {@link ISpatialStructure}.
 */
public interface ISpatialObject {
	/**
	 * @return Returns a AABB fully enclosing the object.
	 */
	AABB getAABB();
}
