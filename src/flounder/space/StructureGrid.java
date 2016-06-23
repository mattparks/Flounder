package flounder.space;

import flounder.maths.*;
import flounder.physics.*;
import sun.reflect.generics.reflectiveObjects.*;

import java.util.*;

/**
 * Represents a 2D space with a grid.
 */
public class StructureGrid<T extends ISpatialObject> implements ISpatialStructure<T> {
	private final int tileSize;
	private final int width;
	private final int height;
	private final List<T>[] tiles;

	/**
	 * Creates a new grid.
	 *
	 * @param tileSize How much space each tile in the grid consumes
	 * @param width How many tiles there are on x.
	 * @param height How many tiles there are on y.
	 */
	@SuppressWarnings("unchecked")
	public StructureGrid(int tileSize, int width, int height) {
		this.tileSize = tileSize;
		this.width = width;
		this.height = height;
		this.tiles = new List[width * height];

		for (int i = 0; i < tiles.length; i++) {
			tiles[i] = new ArrayList<T>();
		}
	}

	private void visit(final AABB aabb, final IVisitor<T> visitor) {
		int minX, minY, maxX, maxY;

		if (aabb.getWidth() > width * tileSize) {
			minX = 0;
			maxX = width - 1;
		} else {
			minX = getGridPosMin(aabb.getMinExtents().getX());
			maxX = getGridPosMax(aabb.getMaxExtents().getX());
		}

		if (aabb.getHeight() > height * tileSize) {
			minY = 0;
			maxY = height - 1;
		} else {
			minY = getGridPosMin(aabb.getMinExtents().getY());
			maxY = getGridPosMax(aabb.getMaxExtents().getY());
		}

		for (int j = minY; j <= maxY; j++) {
			for (int i = minX; i <= maxX; i++) {
				visitor.onVisit(getTile(i, j));
			}
		}
	}

	@Override
	public void add(final T object) {
		visit(object.getAABB(), (List<T> tile) -> tile.add(object));
	}

	@Override
	public void remove(final T object) {
		visit(object.getAABB(), (List<T> tile) -> tile.remove(object));
	}

	@Override
	public List<T> getAll(List<T> result) {
		return result;
	}

	private List<T> queryTile(List<T> tile, List<T> result, AABB aabb) {
		Iterator<T> it = tile.iterator();

		while (it.hasNext()) {
			T t = it.next();

			if (t.getAABB().intersects(aabb).isIntersection()) {
				result.add(t);
			}
		}
		return result;
	}

	@Override
	public List<T> queryInFrustum(final List<T> result, final Frustum frustum) {
		throw new NotImplementedException();
	}

	@Override
	public List<T> queryInAABB(final List<T> result, final AABB aabb) {
		visit(aabb, (List<T> tile) -> queryTile(tile, result, aabb));
		return result;
	}

	@Override
	public void clear() {
		for (int i = 0; i < tiles.length; i++) {
			tiles[i].clear();
		}
	}

	private int getGridPosMin(double pos) {
		return (int) Math.floor(pos / tileSize);
	}

	private int getGridPosMax(double pos) {
		return (int) Math.ceil(pos / tileSize);
	}

	private int getTileX(int x) {
		return Maths.floorMod(x, width);
	}

	private int getTileY(int y) {
		return Maths.floorMod(y, height);
	}

	private List<T> getTile(int x, int y) {
		return tiles[getTileX(x) + getTileY(y) * width];
	}

	private interface IVisitor<T> {
		void onVisit(List<T> tile);
	}
}
