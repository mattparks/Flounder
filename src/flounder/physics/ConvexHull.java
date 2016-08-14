package flounder.physics;

import flounder.engine.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;

import java.util.*;

/**
 * http://www.ahristov.com/tutorial/geometry-games/convex-hull.html
 */
public class ConvexHull {
	private static final float POINT_CLOSEST_DISTANCE_SQUARED = 0.0f; // TODO: Set value that works!

	private List<Vector3f> points;
	private Matrix4f modelMatrix;

	public ConvexHull(List<Vector3f> points) {
		this.points = points;
		this.points = quickHull(points);
		this.modelMatrix = new Matrix4f();
	}

	private List<Vector3f> quickHull(List<Vector3f> convexHull) {
		return convexHull;
	}

	public void update(Vector3f position, Vector3f rotation, float scale) {
		Matrix4f.transformationMatrix(position, rotation, scale, modelMatrix);
	}

	public static boolean intersects(ConvexHull left, ConvexHull right) {
		// TODO: Maths!
		return false;
	}
}
