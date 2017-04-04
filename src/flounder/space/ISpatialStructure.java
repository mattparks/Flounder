package flounder.space;

import flounder.physics.*;

import java.util.*;

/**
 * A data structure that stores objects with a notion of flounder.space.
 *
 * @param <T> Some spatial object being stored in the structure.
 */
public interface ISpatialStructure<T extends ISpatialObject> {
	/**
	 * Adds a new object to the spatial structure.
	 *
	 * @param object The object to add.
	 */
	void add(T object);

	/**
	 * Removes an object from the spatial structure.
	 *
	 * @param object The object to remove.
	 */
	void remove(T object);

	/**
	 * Removes all objects from the spatial structure..
	 */
	void clear();

	/**
	 * Gets the size of this structure.
	 *
	 * @return The structures size.
	 */
	int getSize();

	/**
	 * Returns a set of all objects in the spatial structure.
	 *
	 * @return The list specified by of all objects.
	 */
	List<T> getAll();

	/**
	 * Returns a set of all objects in a specific range of the spatial structure.
	 *
	 * @param range The frustum range of space being queried.
	 *
	 * @return The list of all object in range.
	 */
	List<T> queryInFrustum(Frustum range);

	/**
	 * Returns a set of all objects in a specific range of the spatial structure.
	 *
	 * @param range The shape range of space being queried.
	 *
	 * @return The list of all object in range.
	 */
	List<T> queryInBounding(Collider range);

	/**
	 * If the structure contains the object.
	 *
	 * @param object The object to check for.
	 *
	 * @return If the structure contains the object.
	 */
	boolean contains(ISpatialObject object);
}
