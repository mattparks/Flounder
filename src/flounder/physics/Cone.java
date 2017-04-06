package flounder.physics;

import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.models.*;

/**
 * Represents a cone in a 3d space.
 */
public class Cone extends Collider {
	private float radius;
	private float length;
	private float theta;
	private Vector3f position;

	public Cone() {
		this.radius = 1.0f;
		this.length = 1.0f;
		this.theta = 30.0f;
		this.position = new Vector3f();
	}

	public Cone(float radius, float length, float theta, Vector3f position) {
		this.radius = radius;
		this.length = length;
		this.theta = theta;
		this.position = position;
	}

	@Override
	public Collider update(Vector3f position, Vector3f rotation, float scale, Collider destination) {
		if (destination == null || !(destination instanceof Cone)) {
			destination = new Cone();
		}

		Cone cone = (Cone) destination;

		cone.radius = radius * scale;
		cone.length = length * scale;
		cone.theta = theta;
		cone.position.set(position);

		return destination;
	}

	@Override
	public Vector3f resolveCollision(Collider other, Vector3f positionDelta, Vector3f destination) throws IllegalArgumentException {
		return null;
	}

	@Override
	public Collider clone() {
		return new Cone(radius, length, theta, new Vector3f(position));
	}

	@Override
	public IntersectData intersects(Collider other) throws IllegalArgumentException {
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
		return (1.0f / 3.0f) * (float) Math.PI * radius * radius * radius * (float) Math.pow(theta / (2.0f * (float) Math.PI), 2) * (float) Math.sqrt(1.0f - Math.pow(theta / (2.0f * (float) Math.PI), 2));
	}

	@Override
	public float getSurfaceArea() {
		return 0;
	}

	@Override
	public ModelObject getRenderModel() {
		return null;
	}

	@Override
	public Vector3f getRenderCentre(Vector3f destination) {
		return null;
	}

	@Override
	public Vector3f getRenderScale(Vector3f destination) {
		return null;
	}

	@Override
	public Colour getRenderColour(Colour destination) {
		if (destination == null) {
			destination = new Colour();
		}

		return destination.set(0.0f, 1.0f, 1.0f);
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

	public float getTheta() {
		return theta;
	}

	public void setTheta(float theta) {
		this.theta = theta;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	@Override
	public String toString() {
		return "Cone{" +
				"radius=" + radius +
				", length=" + length +
				", theta=" + theta +
				", position=" + position +
				'}';
	}
}
