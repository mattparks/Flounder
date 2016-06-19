/*package flounder.space;

import flounder.maths.vectors.*;
import flounder.physics.*;

import java.util.*;*/

/**
 * Represents a 2D space that can be recursively divided into 4 equal subspaces.
 * <p>
 * Initializes a QuadTree from an AABB.
 *
 * @param aabb Represents the 2D space inside the QuadTree.
 * @param capacity The number of objects that can be added to the QuadTree before it subdivides.
 * <p>
 * Initializes a QuadTree from a source Quadtree.
 * @param source The source quadtree.
 * <p>
 * Initializes a QuadTree from an AABB.
 * @param aabb Represents the 2D space inside the QuadTree.
 * @param capacity The number of objects that can be added to the QuadTree before it subdivides.
 * <p>
 * Initializes a QuadTree from a source Quadtree.
 * @param source The source quadtree.
 * <p>
 * Initializes a QuadTree from an AABB.
 * @param aabb Represents the 2D space inside the QuadTree.
 * @param capacity The number of objects that can be added to the QuadTree before it subdivides.
 * <p>
 * Initializes a QuadTree from a source Quadtree.
 * @param source The source quadtree.
 */
/*public class StructureQuadtree<T extends ISpatialObject> implements ISpatialStructure<T> {
	private StructureQuadtree<T> nodes[];
	private int capacity;
	private List<T> objects;
	private AABB aabb;*/

/**
 * Initializes a QuadTree from an AABB.
 *
 * @param aabb Represents the 2D space inside the QuadTree.
 * @param capacity The number of objects that can be added to the QuadTree before it subdivides.
 */
/*	public StructureQuadtree(AABB aabb, int capacity) {
		this.aabb = aabb;
		this.capacity = capacity;
		objects = new ArrayList<>();
		nodes = null;
	}*/

/**
 * Initializes a QuadTree from a source Quadtree.
 *
 * @param source The source quadtree.
 */
/*	private StructureQuadtree(StructureQuadtree<T> source) {
		this.nodes = source.nodes;
		this.objects = source.objects;
		this.capacity = source.capacity;
		this.aabb = source.aabb;
	}

	@Override
	public void add(T object) {
		if (addInternal(object)) {
			return;
		}

		float dirX = (float) (object.getAABB().getCenterX() - aabb.getCenterX());
		float dirY = (float) (object.getAABB().getCenterY() - aabb.getCenterY());
		expand(dirX, dirY);
		add(object);
	}

	@Override
	public void remove(T object) {
		if (!removeInternal(object)) {
			objects.remove(object);
		}
	}

	@Override
	public void clear() {
		objects.clear();

		if (nodes != null) {
			for (int i = 0; i < nodes.length; i++) {
				nodes[i].clear();
			}
		}

		nodes = null;
	}

	@Override
	public List<T> getAll(List<T> result) {
		return addAll(result);
	}

	@Override
	public List<T> queryInFrustum(List<T> result, Frustum range) {
		if (!aabb.intersects(range)) {
			return result;
		}

		if (range.contains(aabb)) {
			return addAll(result);
		}

		if (nodes != null) {
			for (int i = 0; i < nodes.length; i++) {
				result = nodes[i].queryInFrustum(result, range);
			}
		}

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
		if (!aabb.intersects(range)) {
			return result;
		}

		if (range.contains(aabb)) {
			return addAll(result);
		}

		if (nodes != null) {
			for (int i = 0; i < nodes.length; i++) {
				result = nodes[i].queryInAABB(result, range);
			}
		}

		Iterator<T> it = objects.iterator();

		while (it.hasNext()) {
			T current = it.next();

			if (current.getAABB().intersects(range).isIntersection()) {
				result.add(current);
			}
		}

		return result;
	}

	private void expand(float expandX, float expandY) {
		StructureQuadtree<T> thisAsNode = new StructureQuadtree<>(this);

		float minX = aabb.getMinExtents().getX();
		float minY = aabb.getMinExtents().getY();
		float maxX = aabb.getMaxExtents().getX();
		float maxY = aabb.getMaxExtents().getY();

		float expanseX = maxX - minX;
		float expanseY = maxY - minY;

		nodes = null;
		objects = new ArrayList<>();

		if (expandX <= 0 && expandY <= 0) {
			aabb = new AABB(new Vector2f(minX - expanseX, minY - expanseY), new Vector2f(maxX, maxY));
			subdivide();
			nodes[1] = thisAsNode;
		} else if (expandX <= 0 && expandY > 0) {
			aabb = new AABB(new Vector2f(minX - expanseX, minY), new Vector2f(maxX, maxY + expanseY));
			subdivide();
			nodes[3] = thisAsNode;
		} else if (expandX > 0 && expandY > 0) {
			aabb = new AABB(new Vector2f(minX, minY), new Vector2f(maxX + expanseX, maxY + expanseY));
			subdivide();
			nodes[2] = thisAsNode;
		} else if (expandX > 0 && expandY <= 0) {
			aabb = new AABB(new Vector2f(minX, minY - expanseY), new Vector2f(maxX + expanseX, maxY));
			subdivide();
			nodes[0] = thisAsNode;
		} else {
			throw new AssertionError("Error: QuadTree direction is invalid (?): " + expandX + " (dirX) " + expandY + " (dirY)");
		}
	}

	private boolean addInternal(T object) {
		if (!aabb.contains(object.getAABB())) {
			return false;
		}

		if (nodes == null) {
			if (objects.size() < capacity) {
				objects.add(object);
				return true;
			}

			subdivide();
		}

		if (!addToChildNode(object)) {
			objects.add(object);
		}

		return true;
	}

	@SuppressWarnings("unchecked")
	private void subdivide() {
		float minX = aabb.getMinExtents().getX();
		float minY = aabb.getMinExtents().getY();
		float maxX = aabb.getMaxExtents().getX();
		float maxY = aabb.getMaxExtents().getY();

		float halfXLength = (maxX - minX) / 2.0f;
		float halfYLength = (maxY - minY) / 2.0f;

		nodes = (new StructureQuadtree[4]);

		minY += halfYLength;
		maxX -= halfXLength;
		nodes[0] = new StructureQuadtree<T>(new AABB(new Vector2f(minX, minY), new Vector2f(maxX, maxY)), capacity);

		minX += halfXLength;
		maxX += halfXLength;
		nodes[1] = new StructureQuadtree<T>(new AABB(new Vector2f(minX, minY), new Vector2f(maxX, maxY)), capacity);

		minY -= halfYLength;
		maxY -= halfYLength;
		nodes[3] = new StructureQuadtree<T>(new AABB(new Vector2f(minX, minY), new Vector2f(maxX, maxY)), capacity);

		minX -= halfXLength;
		maxX -= halfXLength;
		nodes[2] = new StructureQuadtree<T>(new AABB(new Vector2f(minX, minY), new Vector2f(maxX, maxY)), capacity);

		reinsertObjects();
	}

	private void reinsertObjects() {
		Iterator<T> it = objects.iterator();

		while (it.hasNext()) {
			if (addToChildNode(it.next())) {
				it.remove();
			}
		}
	}

	private boolean addToChildNode(T object) {
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i].addInternal(object)) {
				return true;
			}
		}

		return false;
	}

	private boolean removeInternal(T object) {
		if (!aabb.contains(object.getAABB())) {
			return false;
		}

		if (objects.remove(object)) {
			return true;
		}

		if (nodes == null) {
			return false;
		}

		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i].removeInternal(object)) {
				prune();
				return true;
			}
		}

		return false;
	}

	private void prune() {
		if (!isNodesEmpty()) {
			return;
		}

		nodes = null;
	}

	private boolean isNodesEmpty() {
		for (int i = 0; i < nodes.length; i++) {
			if (!nodes[i].isEmpty()) {
				return false;
			}
		}

		return true;
	}

	private boolean isEmpty() {
		if (!objects.isEmpty()) {
			return false;
		}

		if (nodes == null) {
			return true;
		}

		return isNodesEmpty();
	}

	private List<T> addAll(List<T> result) {
		result.addAll(objects);

		if (nodes != null) {
			for (int i = 0; i < nodes.length; i++) {
				nodes[i].addAll(result);
			}
		}

		return result;
	}
}*/
