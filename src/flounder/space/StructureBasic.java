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
		this.objects = new ArrayList<T>();
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

		for (T current : objects) {
			if (current.getBounding() == null || current.getBounding().inFrustum(range)) {
				result.add(current);
			}
		}

		return result;
	}

	@Override
	public List<T> queryInBounding(IBounding range) {
		List<T> result = new ArrayList<>();

		for (T current : objects) {
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
