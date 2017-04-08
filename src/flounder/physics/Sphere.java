package flounder.physics;

import flounder.maths.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.resources.*;

import java.util.*;

/**
 * Represents a sphere in a 3d space.
 */
public class Sphere extends Collider {
	private static final MyFile MODEL_FILE = new MyFile(MyFile.RES_FOLDER, "models", "sphere.obj");
	private static final ModelObject MODEL_OBJECT = ModelFactory.newBuilder().setFile(MODEL_FILE).create();

	private float radius;
	private Vector3f position;

	/**
	 * Creates a new Sphere
	 *
	 * @param radius The spheres radius.
	 * @param position The spheres initial position.
	 */
	public Sphere(float radius, Vector3f position) {
		this.radius = radius;
		this.position = position;
	}

	/**
	 * Creates a new sphere
	 *
	 * @param radius The spheres radius.
	 */
	public Sphere(float radius) {
		this.radius = radius;
		this.position = new Vector3f();
	}

	/**
	 * Creates a new unit sphere
	 */
	public Sphere() {
		this.radius = 1.0f;
		this.position = new Vector3f();
	}

	/**
	 * Creates a new sphere from another sphere source.
	 *
	 * @param source The source to create off of.
	 */
	public Sphere(Sphere source) {
		radius = source.radius;
		position = new Vector3f(source.position);
	}

	@Override
	public Collider update(Vector3f position, Vector3f rotation, float scale, Collider destination) {
		if (destination == null || !(destination instanceof Sphere)) {
			destination = new Sphere();
		}

		Sphere sphere = (Sphere) destination;

		sphere.radius = radius * scale;
		sphere.position.set(position);

		return sphere;
	}

	@Override
	public Vector3f resolveCollision(Collider other, Vector3f positionDelta, Vector3f destination) throws IllegalArgumentException {
		if (destination == null) {
			destination = new Vector3f();
		}

		if (other == null || this.equals(other)) {
			return destination;
		}

		if (other instanceof AABB) {
			AABB aabb2 = (AABB) other;
		} else if (other instanceof ConvexHull) {
			ConvexHull hull2 = (ConvexHull) other;
		} else if (other instanceof Sphere) {
			Sphere sphere2 = (Sphere) other;
			float d = sphere2.radius + radius;

			float xDif = position.x - sphere2.position.x;
			float yDif = position.y - sphere2.position.y;
			float zDif = position.z - sphere2.position.z;
			float distance = xDif * xDif + yDif * yDif + zDif * zDif;
		}

		return destination;
	}

	@Override
	public Collider clone() {
		return new Sphere(radius, new Vector3f(position));
	}

	@Override
	public IntersectData intersects(Collider other) throws IllegalArgumentException {
		if (other == null || this.equals(other)) {
			return new IntersectData(true, 0.0f);
		}

		if (other instanceof AABB) {
			AABB aabb = (AABB) other;

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

			return new IntersectData(distanceSquared > 0.0f, (float) Math.sqrt(distanceSquared));
		} else if (other instanceof ConvexHull) {
			ConvexHull hull2 = (ConvexHull) other;
			return new IntersectData(false, 0.0f);
		} else if (other instanceof Sphere) {
			Sphere sphere = (Sphere) other;

			float d = sphere.radius + radius;

			float xDif = position.x - sphere.position.x;
			float yDif = position.y - sphere.position.y;
			float zDif = position.z - sphere.position.z;
			float distance = xDif * xDif + yDif * yDif + zDif * zDif;

			boolean intersects = d * d > distance;
			return new IntersectData(intersects, (d * d) - distance);
		}

		return new IntersectData(false, 0.0f);
	}

	@Override
	public IntersectData intersects(Ray ray) throws IllegalArgumentException {
		float t;

		Vector3f L = Vector3f.subtract(ray.getOrigin(), position, null);

		float a = Vector3f.dot(ray.getCurrentRay(), ray.getCurrentRay());
		float b = 2.0f * (Vector3f.dot(ray.getCurrentRay(), L));
		float c = (Vector3f.dot(L, L)) - (radius * radius);

		float disc = b * b - 4.0f * a * c;

		if (disc < 0.0f) {
			return new IntersectData(false, -1.0f);
		}

		float distSqrt = (float) Math.sqrt(disc);
		float q;

		if (b < 0.0f) {
			q = (-b - distSqrt) / 2.0f;
		} else {
			q = (-b + distSqrt) / 2.0f;
		}

		float t0 = q / a;
		float t1 = c / q;

		if (t0 > t1) {
			float temp = t0;
			t0 = t1;
			t1 = temp;
		}

		if (t1 < 0.0f) {
			return new IntersectData(false, -1.0f);
		}

		if (t0 < 0.0f) {
			t = t1;
		} else {
			t = t0;
		}

		return new IntersectData(true, t);
	}

	@Override
	public boolean inFrustum(Frustum frustum) {
		return frustum.sphereInFrustum(position.x, position.y, position.z, radius);
	}

	@Override
	public boolean contains(Collider other) {
		if (other == null || this.equals(other)) {
			return false;
		}

		if (other instanceof Sphere) {
			Sphere sphere = (Sphere) other;

			return sphere.position.x + sphere.radius - 1.0f <= position.x + radius - 1.0f
					&& sphere.position.x - sphere.radius + radius >= position.x - radius + 1.0f
					&& sphere.position.y + sphere.radius - 1.0f <= position.y + radius - 1.0f
					&& sphere.position.y - sphere.radius + 1.0f >= position.y - radius + 1.0f
					&& sphere.position.z + sphere.radius - 1.0f <= position.z + radius - 1.0f
					&& sphere.position.z - sphere.radius + 1.0f >= position.z - radius + 1.0f;
		}

		return false;
	}

	@Override
	public boolean contains(Vector3f point) {
		return Vector3f.getDistanceSquared(position, point) <= radius * radius;
	}

	@Override
	public float getVolume() {
		return (4.0f / 3.0f) * (float) Math.PI * radius * radius * radius;
	}

	@Override
	public float getSurfaceArea() {
		return 4.0f * (float) Math.PI * radius * radius;
	}

	@Override
	public Matrix3f getInertiaTensor(float mass, Matrix3f destination) {
		if (destination == null) {
			destination = new Matrix3f();
		}

		float diag = 0.4f * mass * radius * radius;
		destination.setIdentity();
		destination.m00 = diag;
		destination.m11 = diag;
		destination.m22 = diag;

		return destination;
	}

	@Override
	public ModelObject getRenderModel() {
		return MODEL_OBJECT;
	}

	@Override
	public Vector3f getRenderCentre(Vector3f destination) {
		if (destination == null) {
			destination = new Vector3f();
		}

		return destination.set(position);
	}

	@Override
	public Vector3f getRenderScale(Vector3f destination) {
		if (destination == null) {
			destination = new Vector3f();
		}

		return destination.set(radius, radius, radius);
	}

	@Override
	public Colour getRenderColour(Colour destination) {
		if (destination == null) {
			destination = new Colour();
		}

		return destination.set(0.0f, 0.0f, 1.0f);
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
	 * Sets the radius of the sphere.
	 *
	 * @param radius The new sphere radius.
	 */
	public void setRadius(float radius) {
		this.radius = radius;
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

		Sphere other = (Sphere) object;

		if (!Objects.equals(radius, other.radius)) {
			return false;
		} else if (!Objects.equals(position, other.position)) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return "Sphere{" + "radius=" + radius + ", position=" + position + '}';
	}
}
