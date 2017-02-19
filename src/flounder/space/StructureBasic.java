package flounder.space;

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
		this.objects = new Vector<>();
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
	public int getSize() {
		return objects.size();
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

			if (current.getBounding() == null || current.getBounding().inFrustum(range)) {
				result.add(current);
			}
		}

		return result;
	}

	@Override
	public List<T> queryInBounding(List<T> result, IBounding range) {
		Iterator<T> it = objects.iterator();

		while (it.hasNext()) {
			T current = it.next();

			if (current.getBounding() != null) {
				if (current.getBounding().intersects(range).isIntersection()) {
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
}
