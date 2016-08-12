package flounder.space;

import flounder.engine.*;
import flounder.physics.*;

import java.util.*;

/**
 * Represents a 3D space.
 */
public class StructureBasic<T extends ISpatialObject> implements ISpatialStructure<T> {
	private Vector<T> objects;

	/**
	 * Initializes a new Basic 3D Structure.
	 */
	public StructureBasic() {
		objects = new Vector<>();
	}

	@Override
	public void add(T object) {
		objects.add(object);
	}

	@Override
	public void remove(T object) {
		objects.remove(object);
	}

	@Override
	public void clear() {
		objects.clear();
	}

	@Override
	public List<T> getAll(List<T> result) {
		objects.iterator().forEachRemaining(result::add);
		return result;
	}

	@Override
	public List<T> queryInFrustum(List<T> result, Frustum range) {
		Iterator<T> it = objects.iterator();

		while (it.hasNext()) {
			T current = it.next();

			if (range.aabbInFrustum(current.getAABB())) {
				result.add(current);
			}
		}

		return result;
	}

	@Override
	public List<T> queryInAABB(List<T> result, AABB range) {
		Iterator<T> it = objects.iterator();

		while (it.hasNext()) {
			T current = it.next();

			if (current.getAABB() != null) {
				if (current.getAABB().intersects(range).isIntersection()) {
					result.add(current);
				}
			} else {
				result.add(current);
			}
		}

		return result;
	}

	/**
	 * Gets a object from its index.
	 *
	 * @param index The index to get the object from.
	 *
	 * @return The object found.
	 */
	public T get(int index) {
		return objects.get(index);
	}

	/**
	 * Gets the size of this structure.
	 *
	 * @return The structures size.
	 */
	public int getSize() {
		return objects.size();
	}
}
