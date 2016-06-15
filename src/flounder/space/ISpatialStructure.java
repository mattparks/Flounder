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
	 * Returns a set of all objects in the spatial structure.
	 *
	 * @return The list specified by of all objects.
	 */
	List<T> getAll();

	/**
	 * Returns a set of all objects in a specific range of the spatial structure.
	 *
	 * @param range The frustum range of flounder.space being queried.
	 *
	 * @return The list of all object in range.
	 */
	List<T> queryInFrustum(Frustum range);

	/**
	 * Returns a set of all objects in a specific range of the spatial structure.
	 *
	 * @param range The aabb range of flounder.space being queried.
	 *
	 * @return The list of all object in range.
	 */
	List<T> queryInAABB(AABB range);
}
