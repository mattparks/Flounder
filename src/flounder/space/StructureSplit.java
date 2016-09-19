package flounder.space;

import flounder.maths.vectors.*;
import flounder.physics.*;

import java.util.*;

/**
 * Represents a 3D space.
 */
public class StructureSplit<T extends ISpatialObject> implements ISpatialStructure<T> {
	private Map<AABB, Vector<T>> objects;
	private List<T> objectsToSort;

	/**
	 * Initializes a new Basic 3D Structure.
	 */
	public StructureSplit() {
		this.objects = new HashMap<>();
		this.objectsToSort = new ArrayList<>();
	}

	public void split(float divisions) {
		for (AABB aabb : objects.keySet()) {
			objectsToSort.addAll(objects.get(aabb));
		}

		objects.clear();
		AABB g = new AABB();

		for (T star : objectsToSort) {
			Vector3f s = star.getBounding().getRenderCentre(null);

			if (s.x < g.getMinExtents().x) {
				g.getMinExtents().x = s.x;
			} else if (s.x > g.getMaxExtents().x) {
				g.getMaxExtents().x = s.x;
			}

			if (s.y < g.getMinExtents().y) {
				g.getMinExtents().y = s.y;
			} else if (s.y > g.getMaxExtents().y) {
				g.getMaxExtents().y = s.y;
			}

			if (s.z < g.getMinExtents().z) {
				g.getMinExtents().z = s.z;
			} else if (s.z > g.getMaxExtents().z) {
				g.getMaxExtents().z = s.z;
			}
		}

		for (int k = 0; k < divisions; k++) {
			for (int j = 0; j < divisions; j++) {
				for (int i = 0; i < divisions; i++) {
					Vector3f minInnerAABB = new Vector3f(
							g.getMinExtents().getX() + i * (float) (g.getWidth() / divisions),
							g.getMinExtents().getY() + j * (float) (g.getHeight() / divisions),
							g.getMinExtents().getZ() + k * (float) (g.getDepth() / divisions)
					);

					Vector3f maxInnerAABB = new Vector3f(
							minInnerAABB.getX() + (float) (g.getWidth() / divisions),
							minInnerAABB.getY() + (float) (g.getHeight() / divisions),
							minInnerAABB.getZ() + (float) (g.getDepth() / divisions)
					);

					objects.put(new AABB(minInnerAABB, maxInnerAABB), new Vector<T>());
				}
			}
		}

		for (T object : objectsToSort) {
			boolean sorted = false;

			for (AABB aabb : objects.keySet()) {
				if (!sorted && aabb.contains(object.getBounding().getRenderCentre(null))) {
					objects.get(aabb).add(object);
					sorted = true;
				}
			}
		}

		objectsToSort.clear();
	}

	@Override
	public void add(T object) {
		objectsToSort.add(object);
	}

	@Override
	public void remove(T object) {
	//	objects.remove(object);
	}

	@Override
	public void clear() {
		objects.clear();
	}

	@Override
	public List<T> getAll(List<T> result) {
		for (AABB aabb : objects.keySet()) {
			result.addAll(objects.get(aabb));
		}

		return result;
	}

	@Override
	public List<T> queryInFrustum(List<T> result, Frustum range) {
		for (AABB aabb : objects.keySet()) {
			if (aabb.inFrustum(range)) {
				for (T object : objects.get(aabb)) {
					if (object.getBounding().inFrustum(range)) {
						result.add(object);
					}
				}
			}
		}

		return result;
	}

	@Override
	public List<T> queryInBounding(List<T> result, IBounding range) {
		for (AABB aabb : objects.keySet()) {
			if (aabb.intersects(range).isIntersection()) {
				for (T object : objects.get(aabb)) {
					if (object.getBounding().intersects(range).isIntersection()) {
						result.add(object);
					}
				}
			}
		}

		return result;
	}

	public Set<AABB> getAABBs() {
		return objects.keySet();
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
