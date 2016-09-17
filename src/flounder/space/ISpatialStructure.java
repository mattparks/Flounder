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
	 * @param result The list to store results into.
	 *
	 * @return The list specified by of all objects.
	 */
	List<T> getAll(List<T> result);

	/**
	 * Returns a set of all objects in a specific range of the spatial structure.
	 *
	 * @param result The list to store results into.
	 * @param range The frustum range of space being queried.
	 *
	 * @return The list of all object in range.
	 */
	List<T> queryInFrustum(List<T> result, Frustum range);

	/**
	 * Returns a set of all objects in a specific range of the spatial structure.
	 *
	 * @param result The list to store results into.
	 * @param range The shape range of space being queried.
	 *
	 * @return The list of all object in range.
	 */
	List<T> queryInBounding(List<T> result, IBounding range);
}
