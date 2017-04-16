package flounder.physics;

import flounder.logger.*;
import flounder.maths.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;
import flounder.models.*;

import java.util.*;

/**
 * This algorithm calculates the convex hull for a given set of points. This is a Java port of the algorithm written in PHP by Jakob Westhoff.
 */
public class QuickHull extends Collider {
	private List<Vector3f> inputPoints;
	private List<Vector3f> hullPoints;
	private Matrix4f modelMatrix;
	private final boolean iterativeProceeding;

	/**
	 * Initializes the QuickHull algorithm with no values.
	 */
	public QuickHull() {
		this.inputPoints = new ArrayList<>();
		this.hullPoints = new ArrayList<>();
		this.modelMatrix = new Matrix4f();
		this.iterativeProceeding = false;
	}

	/**
	 * Initializes the QuickHull algorithm with a point cloud.
	 *
	 * @param points The point cloud to put in.
	 */
	public QuickHull(List<Vector3f> points) {
		this.inputPoints = points;
		this.hullPoints = new ArrayList<>();
		this.modelMatrix = new Matrix4f();
		this.iterativeProceeding = false;
	}

	/**
	 * Initializes the QuickHull algorithm with a point cloud.
	 *
	 * @param points The point cloud to put in.
	 * @param iterativeProceeding Flag that indicates how the algorithm should work. Either imperative or recursive.
	 * You should use an iterative approach if the point set is very large. This will avoid stack overflows at a certain number of points.
	 * The number of stack overflow depends mainly on the structure of the points and the systems architecture (32/64 bit).
	 */
	public QuickHull(List<Vector3f> points, boolean iterativeProceeding) {
		this.inputPoints = points;
		this.hullPoints = new ArrayList<>();
		this.modelMatrix = new Matrix4f();
		this.iterativeProceeding = iterativeProceeding;
	}

	public QuickHull(List<Vector3f> inputPoints, List<Vector3f> hullPoints, boolean iterativeProceeding) {
		this.inputPoints = inputPoints;
		this.hullPoints = hullPoints;
		this.modelMatrix = new Matrix4f();
		this.iterativeProceeding = iterativeProceeding;
	}

	@Override
	public Collider update(Vector3f position, Vector3f rotation, float scale, Collider destination) {
		if (destination == null || !(destination instanceof QuickHull)) {
			destination = new QuickHull();
		}

		QuickHull hull = (QuickHull) destination;

		if (!this.equals(destination)) {
			hull.inputPoints.clear();
			hull.inputPoints.addAll(inputPoints);

			hull.hullPoints.clear();
			hull.hullPoints.addAll(hullPoints);
		}

		Matrix4f.transformationMatrix(position, rotation, scale, hull.modelMatrix);

		// Returns the final Convex Hull.
		return hull;
	}

	@Override
	public Vector3f resolveCollision(Collider other, Vector3f positionDelta, Vector3f destination) throws IllegalArgumentException {
		if (destination == null) {
			destination = new Vector3f();
		}

		if (other == null || this.equals(other)) {
			return destination;
		}

		if (other instanceof QuickHull) {
			QuickHull hull2 = (QuickHull) other;
			// TODO
		}

		return destination;
	}

	@Override
	public Collider clone() {
		return new QuickHull(this.inputPoints, this.hullPoints, this.iterativeProceeding);
	}

	@Override
	public IntersectData intersects(Collider other) throws IllegalArgumentException {
		if (other == null || this.equals(other)) {
			return new IntersectData(true, 0.0f);
		}

		if (other instanceof QuickHull) {
			QuickHull hull2 = (QuickHull) other;
			// TODO
			return new IntersectData(false, 0.0f);
		}

		return null;
	}

	@Override
	public IntersectData intersects(Ray other) throws IllegalArgumentException {
		return null; // Done with AABB / Sphere.
	}

	@Override
	public boolean inFrustum(Frustum frustum) {
		return false; // Done with AABB / Sphere.
	}

	@Override
	public boolean contains(Collider other) throws IllegalArgumentException {
		if (other == null || this.equals(other)) {
			return false;
		}

		if (other instanceof QuickHull) {
			QuickHull hull2 = (QuickHull) other;
			return false; // Done with AABB / Sphere.
		}

		return false;
	}

	@Override
	public boolean contains(Vector3f point) {
		return false; // Done with AABB / Sphere.
	}

	public void loadData(List<Vector3f> points) {
		this.inputPoints.clear();
		this.inputPoints.addAll(points);
		this.hullPoints = getHullPoints();
	}

	public void loadData(float[] vertices) {
		List<Vector3f> points = new ArrayList<>();
		Vector3f v = new Vector3f();
		int w = 0;

		for (int i = 0; i < vertices.length; i++) {
			if (w == 0) {
				v.x = vertices[i];
				w++;
			} else if (w == 1) {
				v.y = vertices[i];
				w++;
			} else if (w == 2) {
				v.z = vertices[i];
				points.add(new Vector3f(v));
				v = new Vector3f();
				w = 0;
			}
		}

		loadData(points);

			FlounderLogger.log("=====================================");
			for (Vector3f hp : hullPoints) {
				FlounderLogger.log(hp);
			}
	}

	/**
	 * Returns the most right/left aligned point.
	 *
	 * @param leftSide True if the most right aligned point is needed. False otherwise.
	 *
	 * @return The point which corresponds to the query.
	 */
	private Vector3f getXPoint(boolean leftSide) {
		Vector3f pivot = inputPoints.get(0);

		for (Vector3f p : inputPoints) {
			if ((leftSide && p.getX() < pivot.getX()) || (!leftSide && p.getX() > pivot.getX())) { // Min search or Max search.
				pivot = p;
			}
		}

		return pivot;
	}

	/**
	 * Starts the algorithm to get all the hull points.
	 *
	 * @return A vector containing all convex hull points.
	 */
	public final List<Vector3f> getHullPoints() {
		if (hullPoints.size() == 0) {
			// This is the first update with the maximum and minimum x value points. The points are definitive points of the convex hull.
			Vector3f pointMaxX = getXPoint(false);
			Vector3f pointMinX = getXPoint(true);

			// We must process both sides of the line, so we *merge* it here.
			hullPoints.addAll(quickHull(inputPoints, pointMinX, pointMaxX));
			hullPoints.addAll(quickHull(inputPoints, pointMaxX, pointMinX));
		}

		return hullPoints;
	}

	/**
	 * Calculates the distance between two points which span a line and a point.
	 * <p>
	 * The returned value is not the correct distance (for performance reasons) value,
	 * but it's sufficient to determine the point with the maximum distance between the line and the point.
	 * The result is a pseudo cross product of the line vector (first to second point).
	 * <p>
	 * Note: result < 0: point is right of the given vector (line)
	 * result > 0: point is left of the given vector (line)
	 * result = 0: point is on the line (colinear)
	 *
	 * @param pointToCalcDistanceTo The point to calculate the distance to.
	 * @param firstLinePoint The first (start) point of the line.
	 * @param secondLinePoint The second (end) point of the line.
	 *
	 * @return The distance between the line and the given point.
	 */
	private double calculateDistanceIndicator(Vector3f pointToCalcDistanceTo, Vector3f firstLinePoint, Vector3f secondLinePoint) {
		double[] vLine = new double[2];
		vLine[0] = secondLinePoint.getX() - firstLinePoint.getX();
		vLine[1] = secondLinePoint.getY() - firstLinePoint.getY();

		double[] vPoint = new double[2];
		vPoint[0] = pointToCalcDistanceTo.getX() - firstLinePoint.getX();
		vPoint[1] = pointToCalcDistanceTo.getY() - firstLinePoint.getY();

		return ((vPoint[1] * vLine[0]) - (vPoint[0] * vLine[1]));
	}

	/**
	 * Calculates the distance between the line (spanned by the first and second point) and *all* the given points.
	 * <p>
	 * Note: Only the points left of the line will be returned.
	 * Every point right of the line or colinear to the line will be deleted.
	 *
	 * @param points The points to calculate the distance to.
	 * @param first The first point of the line.
	 * @param second The second point of the line.
	 *
	 * @return A vector containing all point distances.
	 */
	private List<PointDistance> getPointDistances(List<Vector3f> points, Vector3f first, Vector3f second) {
		List<PointDistance> ptDistanceSet = new ArrayList<>();

		for (Vector3f p : points) {
			double distanceToPoint = calculateDistanceIndicator(p, first, second);

			if (distanceToPoint > 0) {
				ptDistanceSet.add(new PointDistance(p, distanceToPoint));
			}
		}

		return ptDistanceSet;
	}

	/**
	 * Searches for the point which has the maximum distance.
	 *
	 * @param pointDistanceSet The point distance set to search in.
	 *
	 * @return The point with the maximum distance to the given line.
	 */
	private Vector3f getPointWithMaximumDistanceFromLine(List<PointDistance> pointDistanceSet) {
		double maxDistance = 0;
		Vector3f maxPoint = null;

		for (PointDistance pd : pointDistanceSet) {
			if (pd.distance > maxDistance) {
				maxDistance = pd.distance;
				maxPoint = pd.p;
			}
		}

		return maxPoint;
	}

	/**
	 * Runs the QuickHull algorithm on the given points.
	 * The other two points given are used to delimit the search space in left and right side parts.
	 * <p>
	 * Only the points left of the line will be inspected.
	 *
	 * @param points The set of points to analyze.
	 * @param first The first point of the line which is left of the right one.
	 * @param second The second point of the line which is right of the first
	 * one.
	 *
	 * @return A list containing all the convex hull points on one side.
	 */
	private List<Vector3f> quickHull(List<Vector3f> points, Vector3f first, Vector3f second) {
		if (iterativeProceeding) {
			return quickHullIterative(points, first, second);
		} else {
			return quickHullRecursive(points, first, second);
		}
	}

	/**
	 * Rund the algorithm as a recursive variant.
	 *
	 * @param points The set of points to analyze.
	 * @param first The first point of the line which is left of the right one.
	 * @param second The second point of the line which is right of the first
	 * one.
	 *
	 * @return A list containing all the convex hull points on one side.
	 */
	private List<Vector3f> quickHullRecursive(List<Vector3f> points, Vector3f first, Vector3f second) {
		List<PointDistance> pointsLeftOfLine = getPointDistances(points, first, second);
		Vector3f newMaxPoint = getPointWithMaximumDistanceFromLine(pointsLeftOfLine);
		List<Vector3f> pointsToReturn = new ArrayList<>();

		if (newMaxPoint == null) {
			pointsToReturn.add(second);
		} else {
			List<Vector3f> newPoints = new ArrayList<>();

			for (PointDistance pd : pointsLeftOfLine) {
				newPoints.add(pd.p);
			}

			pointsToReturn.addAll(quickHull(newPoints, first, newMaxPoint));
			pointsToReturn.addAll(quickHull(newPoints, newMaxPoint, second));
		}

		return pointsToReturn;
	}

	/**
	 * Rund the algorithm as a iterative variant.
	 *
	 * @param points The set of points to analyze.
	 * @param first The first point of the line which is left of the right one.
	 * @param second The second point of the line which is right of the first
	 * one.
	 *
	 * @return A list containing all the convex hull points on one side.
	 */
	private ArrayList<Vector3f> quickHullIterative(List<Vector3f> points, Vector3f first, Vector3f second) {
		Stack<QuickHullStackCapsule> stack = new Stack<>();
		stack.push(new QuickHullStackCapsule(points, first, second));

		QuickHullStackCapsule currentStackEntry;

		ArrayList<Vector3f> pointsToReturn = new ArrayList<>();

		while (!stack.empty()) {
			currentStackEntry = stack.pop();

			List<Vector3f> stackPoints = currentStackEntry.pointList;
			Vector3f stackFirstPoint = currentStackEntry.firstPoint;
			Vector3f stackSecondPoint = currentStackEntry.secondPoint;

			List<PointDistance> pointsLeftOfLine = getPointDistances(stackPoints, stackFirstPoint, stackSecondPoint);

			Vector3f newPoint = getPointWithMaximumDistanceFromLine(pointsLeftOfLine);

			if (newPoint == null) {
				pointsToReturn.add(stackSecondPoint);
			} else {
				List<Vector3f> newPoints = new ArrayList<>();

				for (PointDistance pd : pointsLeftOfLine) {
					newPoints.add(pd.p);
				}

				// Instead of calling the function recursive push the current points to the stack.
				stack.push(new QuickHullStackCapsule(newPoints, stackFirstPoint, newPoint));
				stack.push(new QuickHullStackCapsule(newPoints, newPoint, stackSecondPoint));
			}
		}

		return pointsToReturn;
	}

	public boolean isLoaded() {
		return !inputPoints.isEmpty() || !hullPoints.isEmpty();
	}

	@Override
	public ModelObject getRenderModel() {
		return null; // Not implemented.
	}

	@Override
	public Vector3f getRenderCentre(Vector3f destination) {
		return destination; // Not implemented.
	}

	@Override
	public Vector3f getRenderRotation(Vector3f destination) {
		return destination; // Not implemented.
	}

	@Override
	public Vector3f getRenderScale(Vector3f destination) {
		return destination; // Not implemented.
	}

	@Override
	public Colour getRenderColour(Colour destination) {
		return destination; // Not implemented.
	}

	/**
	 * Anonymous inner class which encapsulates a point and a distance.
	 */
	private class PointDistance {
		private Vector3f p;
		private double distance;

		/**
		 * Creates a new PointDistance.
		 *
		 * @param point The point.
		 * @param distance The distance to the given point.
		 */
		PointDistance(Vector3f point, double distance) {
			this.p = point;
			this.distance = distance;
		}
	}

	/**
	 * Anonymous inner class which holds a point list specifying the fist and second point.
	 */
	private class QuickHullStackCapsule {
		private List<Vector3f> pointList;
		private Vector3f firstPoint;
		private Vector3f secondPoint;

		/**
		 * Creates a new QuickHullStackCapsule.
		 *
		 * @param points The points to hold.
		 * @param first The first point.
		 * @param second The second point.
		 */
		QuickHullStackCapsule(List<Vector3f> points, Vector3f first, Vector3f second) {
			pointList = points;
			firstPoint = first;
			secondPoint = second;
		}
	}
}
