package flounder.space;

import flounder.engine.*;
import flounder.physics.*;

import java.util.*;

/**
 * Represents a 3D space that can be recursively divided into 8 equal subspaces.
 */
public class StructureOctree<T extends ISpatialObject> implements ISpatialStructure<T> {
	private StructureQuadtree<T> nodes[];
	private float capacity;
	private Vector<T> objects;
	private AABB aabb;

	/**
	 * Initializes a Octree from an AABB.
	 *
	 * @param capacity The number of objects that can be added to the QuadTree before it subdivides.
	 */
	public StructureOctree(float capacity) {
		this.nodes = null;
		this.capacity = capacity;
		this.objects = new Vector<>();
		this.aabb = new AABB();
	}

	/**
	 * Initializes a Octree from a source Octree.
	 *
	 * @param source The source Octree.
	 */
	private StructureOctree(StructureOctree<T> source) {
		this.nodes = source.nodes;
		this.capacity = source.capacity;
		this.objects = source.objects;
		this.aabb = source.aabb;
	}

	@Override
	public void add(T object) {
		objects.add(object);

		if (!aabb.contains(object.getAABB())) {
			AABB.combine(aabb, object.getAABB(), aabb);
		}
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
				if (current.getAABB().intersects(range).isIntersection() || range.contains(current.getAABB())) {
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

	/**
	 * Gets the AABB that surrounds the Octree.
	 *
	 * @return The AABB that surrounds the Octree.
	 */
	public AABB getAABB() {
		return aabb;
	}
}
