package flounder.physics;

import flounder.logger.*;
import flounder.maths.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.resources.*;

import java.util.*;

/**
 * A 3D world-aligned bounding box.
 */
public class OBB extends Collider {
	private static final MyFile MODEL_FILE = new MyFile(MyFile.RES_FOLDER, "models", "aabb.obj");
	private static final ModelObject MODEL_OBJECT = ModelFactory.newBuilder().setFile(MODEL_FILE).create();

	private Vector3f extents;
	private Vector3f position;
	private Vector3f rotation;

	/**
	 * Creates a new blank 3D OBB.
	 */
	public OBB() {
		this(new Vector3f());
	}

	/**
	 * Creates a new OBB based on it's extents.
	 *
	 * @param extents The extents of the box.
	 */
	public OBB(Vector3f extents) {
		this.extents = extents;
		this.position = new Vector3f();
		this.rotation = new Vector3f();
	}

	public OBB(Vector3f extents, Vector3f position, Vector3f rotation) {
		this.extents = extents;
		this.position = position;
		this.rotation = rotation;
	}

	/**
	 * Creates a new OBB from another OBB source.
	 *
	 * @param source The source to create off of.
	 */
	public OBB(OBB source) {
		this.extents = new Vector3f(source.getExtents());
		this.position = new Vector3f();
		this.rotation = new Vector3f();
	}

	public OBB(AABB source) {
		this.extents = new Vector3f(source.getWidth(), source.getHeight(), source.getDepth());
		this.position = new Vector3f(source.getCentreX(), source.getCentreY(), source.getCentreZ());
		this.rotation = new Vector3f();
	}

	@Override
	public Collider update(Vector3f position, Vector3f rotation, float scale, Collider destination) {
		if (destination == null || !(destination instanceof OBB)) {
			destination = new OBB();
		}

		OBB obb = (OBB) destination;

		// Sets the destinations values to the sources.
		obb.extents.set(extents);

		// Scales the dimensions for the OBB.
		obb.extents.scale(scale);

		// Sets the OBBs position and rotation.
		obb.position.set(this.position);
		obb.position.scale(scale);
		Vector3f.add(position, obb.position, obb.position);
		// obb.position.set(position);
		obb.rotation.set(rotation);

		// Returns the final OBB.
		return obb;
	}

	@Override
	public Vector3f resolveCollision(Collider other, Vector3f positionDelta, Vector3f destination) throws IllegalArgumentException {
		if (destination == null) {
			destination = new Vector3f();
		}

		if (other == null || this.equals(other)) {
			return destination;
		}

		if (other instanceof OBB) {
			OBB obb2 = (OBB) other;
		}

		return destination;
	}

	@Override
	public Collider clone() {
		return new OBB(new Vector3f(extents), new Vector3f(position), new Vector3f(rotation));
	}

	@Override
	public IntersectData intersects(Collider other) throws IllegalArgumentException {
		if (other == null || this.equals(other)) {
			return new IntersectData(true, 0.0f);
		}

		if (other instanceof OBB) {
			return new IntersectData(false, 0.0f);
		}

		return new IntersectData(false, 0.0f);
	}

	@Override
	public IntersectData intersects(Ray ray) throws IllegalArgumentException {
		return new IntersectData(false, 0.0f);
	}

	@Override
	public boolean inFrustum(Frustum frustum) {
		return true;
	}

	@Override
	public boolean contains(Collider other) throws IllegalArgumentException {
		if (other == null || this.equals(other)) {
			return false;
		}

		if (other instanceof OBB) {
			OBB obb2 = (OBB) other;
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
		return extents.getX() * extents.getZ() * extents.getY();
	}

	@Override
	public float getSurfaceArea() {
		return (2.0f * extents.getX() * extents.getY()) + (2.0f * extents.getZ() * extents.getY());
	}

	@Override
	public Matrix3f getInertiaTensor(float mass, Matrix3f destination) {
		if (destination == null) {
			destination = new Matrix3f();
		}

		float factor = (1.0f / 3.0f) * mass;
		float xSquare = extents.x;
		float ySquare = extents.y;
		float zSquare = extents.z;
		destination.setIdentity();
		destination.m00 = factor * (ySquare + zSquare);
		destination.m11 = factor * (xSquare + zSquare);
		destination.m22 = factor * (xSquare + ySquare);

		return destination;
	}

	public Vector3f getExtents() {
		return extents;
	}

	public void setExtents(Vector3f extents) {
		this.extents = extents;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Vector3f getRotation() {
		return rotation;
	}

	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
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
	public Vector3f getRenderRotation(Vector3f destination) {
		if (destination == null) {
			destination = new Vector3f();
		}

		return destination.set(rotation);
	}

	@Override
	public Vector3f getRenderScale(Vector3f destination) {
		if (destination == null) {
			destination = new Vector3f();
		}

		return destination.set(extents.x / 2.0f, extents.y / 2.0f, extents.z / 2.0f);
	}

	@Override
	public Colour getRenderColour(Colour destination) {
		if (destination == null) {
			destination = new Colour();
		}

		return destination.set(0.5f, 0.5f, 0.0f);
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 79 * hash + Objects.hashCode(extents);
		hash = 79 * hash + Objects.hashCode(position);
		hash = 79 * hash + Objects.hashCode(rotation);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		} else if (super.getClass() != object.getClass()) {
			return false;
		}

		OBB other = (OBB) object;

		if (!Objects.equals(extents, other.extents)) {
			return false;
		} else if (!Objects.equals(position, other.position)) {
			return false;
		} else if (!Objects.equals(rotation, other.rotation)) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return "OBB{" +
				"extents=" + extents +
				", position=" + position +
				", rotation=" + rotation +
				'}';
	}
}