package flounder.space;

import flounder.logger.*;
import flounder.physics.*;

import java.util.*;
import java.util.function.*;

/**
 * Represents a 3D space.
 */
public class StructureBasic<T extends ISpatialObject> implements ISpatialStructure<T> {
	private List<T> objects;
	private List<T> clones;

	/**
	 * Initializes a new Basic 3D Structure.
	 */
	public StructureBasic() {
		this.objects = new ArrayList<>();
		this.clones = new ArrayList<>();
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
		if (result == null) {
			result = new ArrayList<>();
		}

		result.addAll(objects);
		return result;
	}

	@Override
	public void foreach(Consumer<? super T> action) {
		clones.clear();
		clones.addAll(objects);
		clones.forEach(action);
	}

	@Override
	public Iterator<T> iterator() {
		clones.clear();
		clones.addAll(objects);
		return clones.iterator();
	}

	@Override
	public List<T> queryInFrustum(Frustum range, List<T> result) {
		if (result == null) {
			result = new ArrayList<>();
		}

		if (objects == null) {
			return result;
		}

		clones.clear();
		clones.addAll(objects);

		for (T current : clones) {
			if (current != null && (current.getCollider() == null || current.getCollider().inFrustum(range))) {
				result.add(current);
			}
		}

		return result;
	}

	@Override
	public List<T> queryInBounding(Collider range, List<T> result) {
		if (result == null) {
			result = new ArrayList<>();
		}

		if (objects == null) {
			return result;
		}

		clones.clear();
		clones.addAll(objects);

		for (T current : clones) {
			if (current.getCollider() == null || (range.intersects(current.getCollider()).isIntersection() || range.contains(current.getCollider()))) {
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
