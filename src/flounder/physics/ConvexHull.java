package flounder.physics;

import flounder.maths.matrices.*;
import flounder.maths.vectors.*;

import java.util.*;

public class ConvexHull {
	private List<Vector3f> points;
	private Matrix4f modelMatrix;

	public ConvexHull() {
		this.points = new ArrayList<>();
		this.modelMatrix = new Matrix4f();
	}

	public ConvexHull(List<Vector3f> points) {
		this.points = quickHull(points);
		this.modelMatrix = new Matrix4f();
	}

	public void setCalculatedPoints(List<Vector3f> points) {
		this.points.clear();
		this.points = points;
	}

	public List<Vector3f> getPoints() {
		return points;
	}

	public boolean isEmpty() {
		return points.isEmpty();
	}

	private List<Vector3f> quickHull(List<Vector3f> convexHull) {
		List<Vector3f> finalHull = new ArrayList<>();
		return finalHull;
	}

	public static void update(ConvexHull destination, Vector3f position, Vector3f rotation, float scale) {
		Matrix4f.transformationMatrix(position, rotation, scale, destination.modelMatrix);
	}

	public static boolean intersects(ConvexHull left, ConvexHull right) {
		// TODO: Maths!
		return true;
	}
}
