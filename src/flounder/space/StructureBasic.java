package flounder.space;

import flounder.physics.*;

import java.util.*;

/**
 * Represents a 3D space.
 */
public class StructureBasic<T extends ISpatialObject> implements ISpatialStructure<T> {
	private List<T> objects;

	/**
	 * Initializes a new Basic 3D Structure.
	 */
	public StructureBasic() {
		this.objects = new ArrayList<>();
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
	public List<T> getAll() {
		return objects;
	}

	@Override
	public List<T> queryInFrustum(Frustum range) {
		List<T> result = new ArrayList<>();

		if (objects == null) {
			return result;
		}

		for (T current : objects) {
			if (current != null && (current.getCollider() == null || current.getCollider().inFrustum(range))) {
				result.add(current);
			}
		}

		return result;
	}

	@Override
	public List<T> queryInBounding(Collider range) {
		List<T> result = new ArrayList<>();

		if (objects == null) {
			return result;
		}

		for (T current : objects) {
			if (current != null && current.getCollider() != null && (current.getCollider().intersects(range).isIntersection() || range.contains(current.getCollider()))) {
				result.add(current);
			}
		}

		return result;
	}

	@Override
	public boolean contains(ISpatialObject object) {
		return objects.contains(object);
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
