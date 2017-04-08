package flounder.physics;

import flounder.maths.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;
import flounder.models.*;

public class ConvexHull extends Collider {
	@Override
	public Collider update(Vector3f position, Vector3f rotation, float scale, Collider destination) {
		if (destination == null || !(destination instanceof ConvexHull)) {
			destination = new ConvexHull();
		}

		ConvexHull hull = (ConvexHull) destination;

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

		if (other instanceof AABB) {
			AABB aabb2 = (AABB) other;
		} else if (other instanceof ConvexHull) {
			ConvexHull hull2 = (ConvexHull) other;
		} else if (other instanceof Sphere) {
			Sphere sphere2 = (Sphere) other;
		}

		return destination;
	}

	@Override
	public Collider clone() {
		return null;
	}

	@Override
	public IntersectData intersects(Collider other) throws IllegalArgumentException {
		if (other == null || this.equals(other)) {
			return new IntersectData(true, 0.0f);
		}

		if (other instanceof AABB) {
			AABB aabb2 = (AABB) other;
			return new IntersectData(false, 0.0f);
		} else if (other instanceof ConvexHull) {
			ConvexHull hull2 = (ConvexHull) other;
			return new IntersectData(false, 0.0f);
		} else if (other instanceof Sphere) {
			Sphere sphere2 = (Sphere) other;
			return new IntersectData(false, 0.0f);
		}

		return null;
	}

	@Override
	public IntersectData intersects(Ray other) throws IllegalArgumentException {
		return null;
	}

	@Override
	public boolean inFrustum(Frustum frustum) {
		return false;
	}

	@Override
	public boolean contains(Collider other) throws IllegalArgumentException {
		if (other == null || this.equals(other)) {
			return false;
		}

		if (other instanceof AABB) {
			AABB aabb2 = (AABB) other;
			return false;
		} else if (other instanceof ConvexHull) {
			ConvexHull hull2 = (ConvexHull) other;
			return false;
		} else if (other instanceof Sphere) {
			Sphere sphere2 = (Sphere) other;
			return false;
		}

		return false;
	}

	@Override
	public boolean contains(Vector3f point) {
		return false;
	}

	@Override
	public float getVolume() {
		return 0;
	}

	@Override
	public float getSurfaceArea() {
		return 0;
	}

	@Override
	public Matrix3f getInertiaTensor(float mass, Matrix3f destination) {
		if (destination == null) {
			destination = new Matrix3f();
		}

		return destination;
	}

	@Override
	public ModelObject getRenderModel() {
		return null;
	}

	@Override
	public Vector3f getRenderCentre(Vector3f destination) {
		if (destination == null) {
			destination = new Vector3f();
		}

		return destination.set(0.0f, 0.0f, 0.0f);
	}

	@Override
	public Vector3f getRenderScale(Vector3f destination) {
		if (destination == null) {
			destination = new Vector3f();
		}

		return destination.set(1.0f, 1.0f, 1.0f);
	}

	@Override
	public Colour getRenderColour(Colour destination) {
		if (destination == null) {
			destination = new Colour();
		}

		return destination.set(0.0f, 1.0f, 0.0f);
	}
}
