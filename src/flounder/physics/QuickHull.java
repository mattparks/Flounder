package flounder.physics;

import flounder.maths.matrices.*;
import flounder.maths.vectors.*;

import java.util.*;

/**
 * This algorithm calculates the convex hull for a given set of points. This is a Java port of the algorithm written in PHP by Jakob Westhoff.
 */
public class QuickHull {
	private List<Vector3f> inputPoints;
	private List<Vector3f> hullPoints;
	private Matrix4f modelMatrix;
	private final boolean iterativeProceeding;

	/**
	 * Initializes the QuickHull algorithm with no values.
	 */
	public QuickHull() {
		inputPoints = new ArrayList<>();
		hullPoints = new ArrayList<>();
		modelMatrix = new Matrix4f();
		iterativeProceeding = false;
	}

	/**
	 * Initializes the QuickHull algorithm with a point cloud.
	 *
	 * @param points The point cloud to put in.
	 */
	public QuickHull(List<Vector3f> points) {
		inputPoints = points;
		hullPoints = new ArrayList<>();
		modelMatrix = new Matrix4f();
		iterativeProceeding = false;
	}

	/**
	 * Initializes the QuickHull algorithm with a point cloud.
	 *
	 * @param points The point cloud to put in.
	 * @param iterative Flag to indicate how the algorithm should work.
	 */
	public QuickHull(List<Vector3f> points, boolean iterative) {
		inputPoints = points;
		hullPoints = new ArrayList<>();
		modelMatrix = new Matrix4f();
		iterativeProceeding = iterative;
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
			// This is the first run with the maximum and minimum x value points. The points are definitive points of the convex hull.
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

	/**
	 * Creates an QuickHull equivalent to this, but in a new position and rotation.
	 *
	 * @param source The source QuickHull.
	 * @param destination The destination QuickHull or null if a new QuickHull is to be created.
	 * @param position The amount to move.
	 * @param rotation The amount to rotate.
	 * @param scale The amount to scale the object.
	 *
	 * @return An QuickHull equivalent to this, but in a new position.
	 */
	public static QuickHull recalculate(QuickHull source, QuickHull destination, Vector3f position, Vector3f rotation, float scale) {
		if (destination == null) {
			destination = new QuickHull();
		}

		destination.inputPoints = source.inputPoints;
		destination.hullPoints = source.hullPoints;
		Matrix4f.transformationMatrix(position, rotation, scale, destination.modelMatrix);
		return destination;
	}

	/**
	 * Tests whether another QuickHull is intersecting this one.
	 *
	 * @param other The QuickHull being tested for intersection
	 *
	 * @return True if {@code other} is intersecting this QuickHull, false otherwise.
	 */
	public boolean intersects(QuickHull other) {
		// TODO: Maths!
		return true;
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
