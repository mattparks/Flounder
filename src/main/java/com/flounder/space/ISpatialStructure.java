package com.flounder.space;

import com.flounder.physics.*;

import java.util.*;
import java.util.function.*;

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
	 * @param result The list to store the data into.
	 *
	 * @return The list specified by of all objects.
	 */
	List<T> getAll(List<T> result);

	/**
	 * Runs this foreach action on list directly.
	 *
	 * @param action The action to preform.
	 */
	void foreach(Consumer<? super T> action);

	/**
	 * Gets the iterator for this structure directly.
	 *
	 * @return The iterator.
	 */
	Iterator<T> iterator();

	/**
	 * Returns a set of all objects in a specific range of the spatial structure.
	 *
	 * @param range The frustum range of space being queried.
	 * @param result The list to store the data into.
	 *
	 * @return The list of all object in range.
	 */
	List<T> queryInFrustum(Frustum range, List<T> result);

	/**
	 * Returns a set of all objects in a specific range of the spatial structure.
	 *
	 * @param range The shape range of space being queried.
	 * @param result The list to store the data into.
	 *
	 * @return The list of all object in range.
	 */
	List<T> queryInBounding(Collider range, List<T> result);

	/**
	 * If the structure contains the object.
	 *
	 * @param object The object to check for.
	 *
	 * @return If the structure contains the object.
	 */
	boolean contains(ISpatialObject object);
}
