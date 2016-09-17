package flounder.physics;

import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.resources.*;
import flounder.space.*;

import java.util.*;

public class Sphere implements IShape<Sphere> {
	private static final MyFile MODEL_FILE = new MyFile(MyFile.RES_FOLDER, "models", "sphere.obj");

	private float radius;
	private Vector3f position;

	/**
	 * Creates a new Sphere
	 *
	 * @param radius The spheres radius.
	 * @param position The spheres inital position.
	 */
	public Sphere(float radius, Vector3f position) {
		this.radius = radius;
		this.position = position;
	}

	/**
	 * Creates a new Sphere
	 *
	 * @param radius The spheres radius.
	 */
	public Sphere(float radius) {
		this.radius = radius;
		this.position = new Vector3f();
	}

	/**
	 * Creates a new Sphere from another Sphere source.
	 *
	 * @param source The source to create off of.
	 */
	public Sphere(Sphere source) {
		radius = source.radius;
		position = new Vector3f(source.position);
	}

	/**
	 * Creates an Sphere equivalent to this, but in a new position and scale.
	 *
	 * @param source The source Sphere.
	 * @param position The amount to move.
	 * @param scale The amount to scale the object.
	 * @param destination The destination Sphere or null if a new Sphere is to be created.
	 *
	 * @return An Sphere equivalent to this, but in a new position.
	 */
	public static Sphere recalculate(Sphere source, Vector3f position, float scale, Sphere destination) {
		if (destination == null) {
			destination = new Sphere(1.0f);
		}

		destination.radius = source.radius * scale;
		destination.position.set(position);
		return destination;
	}

	@Override
	public boolean contains(Sphere other) {
		return other.position.x + other.radius - 1.0f <= position.x + radius - 1.0f
				&& other.position.x - other.radius + radius >= position.x - radius + 1.0f
				&& other.position.y + other.radius - 1.0f <= position.y + radius - 1.0f
				&& other.position.y - other.radius + 1.0f >= position.y - radius + 1.0f
				&& other.position.z + other.radius - 1.0f <= position.z + radius - 1.0f
				&& other.position.z - other.radius + 1.0f >= position.z - radius + 1.0f;
	}

	@Override
	public IntersectData intersects(Sphere other) {
		if (other == null) {
			throw new IllegalArgumentException("Null Sphere collider.");
		} else if (equals(other)) {
			return new IntersectData(true, 0.0f);
		}

		float d = other.radius + radius;

		float xDif = position.x - other.position.x;
		float yDif = position.y - other.position.y;
		float zDif = position.z - other.position.z;
		float distance = xDif * xDif + yDif * yDif + zDif * zDif;

		boolean intersects = d * d > distance;
		return new IntersectData(intersects, (d * d) - distance);
	}

	/**
	 * Tests if a AABB intersects this Sphere.
	 *
	 * @param aabb The AABB to test with.
	 *
	 * @return If the AABB intersects this sphere.
	 */
	public boolean intersectsAABB(AABB aabb) {
		float distanceSquared = radius * radius;

		if (position.x < aabb.getMinExtents().x) {
			distanceSquared -= Math.pow(position.x - aabb.getMinExtents().x, 2);
		} else if (position.x > aabb.getMaxExtents().x) {
			distanceSquared -= Math.pow(position.x - aabb.getMaxExtents().x, 2);
		}

		if (position.y < aabb.getMinExtents().x) {
			distanceSquared -= Math.pow(position.y - aabb.getMinExtents().y, 2);
		} else if (position.x > aabb.getMaxExtents().x) {
			distanceSquared -= Math.pow(position.y - aabb.getMaxExtents().y, 2);
		}

		if (position.z < aabb.getMinExtents().x) {
			distanceSquared -= Math.pow(position.z - aabb.getMinExtents().z, 2);
		} else if (position.z > aabb.getMaxExtents().x) {
			distanceSquared -= Math.pow(position.z - aabb.getMaxExtents().z, 2);
		}

		return distanceSquared > 0.0f;
	}

	@Override
	public boolean intersectsRay(Ray ray) {
		double t;

		Vector3f L = Vector3f.subtract(ray.getOrigin(), position, null);

		double a = Vector3f.dot(ray.getCurrentRay(), ray.getCurrentRay());
		double b = 2.0 * (Vector3f.dot(ray.getCurrentRay(), L));
		double c = (Vector3f.dot(L, L)) - (radius * radius);

		double disc = b * b - 4.0 * a * c;

		if (disc < 0.0) {
			return false;
			//	return -1.0;
		}

		double distSqrt = Math.sqrt(disc);
		double q;

		if (b < 0.0) {
			q = (-b - distSqrt) / 2.0;
		} else {
			q = (-b + distSqrt) / 2.0;
		}

		double t0 = q / a;
		double t1 = c / q;

		if (t0 > t1) {
			double temp = t0;
			t0 = t1;
			t1 = temp;
		}

		if (t1 < 0.0) {
			return false;
			// return -1.0;
		}

		if (t0 < 0.0) {
			t = t1;
		} else {
			t = t0;
		}

		return true;
		// return t;
	}

	@Override
	public boolean contains(Vector3f point) {
		float xDif = position.x - point.x;
		float yDif = position.y - point.y;
		float zDif = position.z - point.z;
		float distance = xDif * xDif + yDif * yDif + zDif * zDif;
		return radius * radius < distance;
	}

	@Override
	public boolean inFrustum(Frustum frustum) {
		return frustum.sphereInFrustum(position.x, position.y, position.z, radius);
	}

	/**
	 * Gets the radius of the sphere.
	 *
	 * @return The radius of the sphere.
	 */
	public float getRadius() {
		return radius;
	}

	/**
	 * Gets the radius of the position.
	 *
	 * @return The radius of the position.
	 */
	public Vector3f getPosition() {
		return position;
	}

	@Override
	public Model getRenderModel() {
		return Model.newModel(MODEL_FILE).create();
	}

	@Override
	public Vector3f getRenderCentre() {
		return position;
	}

	@Override
	public Vector3f getRenderScale() {
		return new Vector3f(radius, radius, radius);
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 79 * hash + Objects.hashCode(radius);
		hash = 79 * hash + Objects.hashCode(position);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		} else if (super.getClass() != object.getClass()) {
			return false;
		}

		Sphere that = (Sphere) object;

		if (!Objects.equals(radius, that.radius)) {
			return false;
		} else if (!Objects.equals(position, that.position)) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return "Sphere{" + "radius=" + radius + ", position=" + position + '}';
	}
}
