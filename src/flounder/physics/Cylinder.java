package flounder.physics;

import flounder.maths.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.resources.*;

/**
 * Represents a cone in a 3d space.
 */
public class Cylinder extends Collider {
	private static final MyFile MODEL_FILE = new MyFile(MyFile.RES_FOLDER, "models", "cylinder.obj");
	private static final ModelObject MODEL_OBJECT = ModelFactory.newBuilder().setFile(MODEL_FILE).create();

	private float radius;
	private float length;
	private Vector3f position;

	public Cylinder() {
		this.radius = 1.0f;
		this.length = 1.0f;
		this.position = new Vector3f();
	}

	public Cylinder(float radius, float length) {
		this.radius = radius;
		this.length = length;
		this.position = new Vector3f();
	}

	public Cylinder(float radius, float length, Vector3f position) {
		this.radius = radius;
		this.length = length;
		this.position = position;
	}

	@Override
	public Collider update(Vector3f position, Vector3f rotation, float scale, Collider destination) {
		if (destination == null || !(destination instanceof Cylinder)) {
			destination = new Cylinder();
		}

		Cylinder cylinder = (Cylinder) destination;

		cylinder.radius = radius * scale;
		cylinder.length = length * scale;
		cylinder.position.set(position);

		return destination;
	}

	@Override
	public Vector3f resolveCollision(Collider other, Vector3f positionDelta, Vector3f destination) throws IllegalArgumentException {
		return destination;
	}

	@Override
	public Collider clone() {
		return new Cylinder(radius, length, new Vector3f(position));
	}

	@Override
	public IntersectData intersects(Collider other) throws IllegalArgumentException {
		if (other == null) {
			throw new IllegalArgumentException("Null Collider.");
		} else if (this.equals(other)) {
			return new IntersectData(true, 0.0f);
		}

		if (other instanceof AABB) {
			return new IntersectData(false, 0.0f); // TODO
		} else if (other instanceof Cone) {
			return new IntersectData(false, 0.0f); // TODO
		} else if (other instanceof Cylinder) {
			return new IntersectData(false, 0.0f); // TODO
		} else if (other instanceof Sphere) {
			return new IntersectData(false, 0.0f); // TODO
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
		return false;
	}

	@Override
	public boolean contains(Vector3f point) {
		return false;
	}

	@Override
	public float getVolume() {
		return (float) Math.PI * radius * radius * radius * length;
	}

	@Override
	public float getSurfaceArea() {
		return (2.0f * (float) Math.PI * radius * length) + (2.0f * (float) Math.PI * radius * radius);
	}

	@Override
	public Matrix3f getInertiaTensor(float mass, Matrix3f destination) {
		if (destination == null) {
			destination = new Matrix3f();
		}

		float diag = (1.0f / 12.0f) * mass * (3.0f * radius * radius + length * length);
		destination.setIdentity();
		destination.m00 = diag;
		destination.m11 = 0.5f * mass * radius * radius;
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

		return destination.set(radius, length, radius);
	}

	@Override
	public Colour getRenderColour(Colour destination) {
		if (destination == null) {
			destination = new Colour();
		}

		return destination.set(0.0f, 0.0f, 1.0f);
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public float getLength() {
		return length;
	}

	public void setLength(float length) {
		this.length = length;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	@Override
	public String toString() {
		return "Cylinder{" +
				"radius=" + radius +
				", length=" + length +
				", position=" + position +
				'}';
	}
}
