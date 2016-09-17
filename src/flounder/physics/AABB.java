package flounder.physics;

import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.resources.*;
import flounder.space.*;

import java.util.*;

/**
 * A 3D axis-aligned bounding box.
 */
public class AABB implements IShape<AABB> {
	private static final MyFile MODEL_FILE = new MyFile(MyFile.RES_FOLDER, "models", "aabb.obj");

	private Vector3f minExtents;
	private Vector3f maxExtents;

	/**
	 * Creates a new blank 3D AABB.
	 */
	public AABB() {
		this(new Vector3f(), new Vector3f());
	}

	/**
	 * Creates a new AABB based on it's extents.
	 *
	 * @param minExtents The minimum extent of the box.
	 * @param maxExtents The minimum extent of the box.
	 */
	public AABB(Vector3f minExtents, Vector3f maxExtents) {
		this.minExtents = minExtents;
		this.maxExtents = maxExtents;
	}

	/**
	 * Creates a new AABB from another AABB source.
	 *
	 * @param source The source to create off of.
	 */
	public AABB(AABB source) {
		this.minExtents = new Vector3f(source.getMinExtents());
		this.maxExtents = new Vector3f(source.getMaxExtents());
	}

	/**
	 * Creates a new AABB equivalent to this, scaled away from the centre origin.
	 *
	 * @param source The source AABB.
	 * @param destination The destination AABB or null if a new AABB is to be created.
	 * @param scale Amount to scale up the AABB.
	 *
	 * @return A new AABB, scaled by the specified amounts.
	 */
	public static AABB scale(AABB source, AABB destination, Vector3f scale) {
		return scale(source, destination, scale.x, scale.y, scale.z);
	}

	/**
	 * Creates a new AABB equivalent to this, scaled away from the centre origin.
	 *
	 * @param source The source AABB.
	 * @param destination The destination AABB or null if a new AABB is to be created.
	 * @param scaleX Amount to scale up the AABB on X.
	 * @param scaleY Amount to scale up the AABB on Y.
	 * @param scaleZ Amount to scale up the AABB on Z.
	 *
	 * @return A new AABB, scaled by the specified amounts.
	 */
	public static AABB scale(AABB source, AABB destination, float scaleX, float scaleY, float scaleZ) {
		if (destination == null) {
			destination = new AABB();
		}

		destination.setMinExtents(source.minExtents.x * scaleX, source.minExtents.y * scaleY, source.minExtents.z * scaleZ);
		destination.setMaxExtents(source.maxExtents.x * scaleX, source.maxExtents.y * scaleY, source.maxExtents.z * scaleZ);
		return destination;
	}

	/**
	 * Creates a new AABB equivalent to this, but scaled away from the origin by a certain amount.
	 *
	 * @param source The source AABB.
	 * @param destination The destination AABB or null if a new AABB is to be created.
	 * @param expand Amount to scale up the AABB.
	 *
	 * @return A new AABB, scaled by the specified amounts.
	 */
	public static AABB expand(AABB source, AABB destination, Vector3f expand) {
		return expand(source, destination, expand.x, expand.y, expand.z);
	}

	/**
	 * Creates a new AABB equivalent to this, but scaled away from the origin by a certain amount.
	 *
	 * @param source The source AABB.
	 * @param destination The destination AABB or null if a new AABB is to be created.
	 * @param expandX Amount to scale up the AABB on X.
	 * @param expandY Amount to scale up the AABB on Y.
	 * @param expandZ Amount to scale up the AABB on Z.
	 *
	 * @return A new AABB, scaled by the specified amounts.
	 */
	public static AABB expand(AABB source, AABB destination, float expandX, float expandY, float expandZ) {
		if (destination == null) {
			destination = new AABB();
		}

		destination.minExtents.set(source.minExtents.x - expandX, source.minExtents.y - expandY, source.minExtents.z - expandZ);
		destination.maxExtents.set(source.maxExtents.x + expandX, source.maxExtents.y + expandY, source.maxExtents.z + expandZ);

		return destination;
	}

	/**
	 * Creates an AABB that bounds both this AABB and another AABB.
	 *
	 * @param left The left source AABB.
	 * @param right The right source AABB.
	 * @param destination The destination AABB or null if a new AABB is to be created.
	 *
	 * @return An AABB that bounds both this AABB and {@code other}.
	 */
	public static AABB combine(AABB left, AABB right, AABB destination) {
		if (destination == null) {
			destination = new AABB();
		}

		float newMinX = Math.min(left.minExtents.x, right.getMinExtents().x);
		float newMinY = Math.min(left.minExtents.y, right.getMinExtents().y);
		float newMinZ = Math.min(left.minExtents.z, right.getMinExtents().z);
		float newMaxX = Math.max(left.maxExtents.x, right.getMaxExtents().x);
		float newMaxY = Math.max(left.maxExtents.y, right.getMaxExtents().y);
		float newMaxZ = Math.max(left.maxExtents.z, right.getMaxExtents().z);

		destination.minExtents.set(newMinX, newMinY, newMinZ);
		destination.maxExtents.set(newMaxX, newMaxY, newMaxZ);

		return destination;
	}

	/**
	 * Creates a new AABB equivalent to this, but stretched by a certain amount.
	 *
	 * @param source The source AABB.
	 * @param destination The destination AABB or null if a new AABB is to be created.
	 * @param stretch The amount to stretch.
	 *
	 * @return A new AABB, stretched by the specified amounts.
	 */
	public static AABB stretch(AABB source, AABB destination, Vector3f stretch) {
		return stretch(source, destination, stretch.x, stretch.y, stretch.z);
	}

	/**
	 * Creates a new AABB equivalent to this, but stretched by a certain amount.
	 *
	 * @param source The source AABB.
	 * @param destination The destination AABB or null if a new AABB is to be created.
	 * @param stretchX The amount to stretch on the X.
	 * @param stretchY The amount to stretch on the Y.
	 * @param stretchZ The amount to stretch on the Z.
	 *
	 * @return A new AABB, stretched by the specified amounts.
	 */
	public static AABB stretch(AABB source, AABB destination, float stretchX, float stretchY, float stretchZ) {
		if (destination == null) {
			destination = new AABB();
		}

		float newMinX, newMaxX, newMinY, newMaxY, newMinZ, newMaxZ;

		if (stretchX < 0) {
			newMinX = source.minExtents.x + stretchX;
			newMaxX = source.maxExtents.x;
		} else {
			newMinX = source.minExtents.x;
			newMaxX = source.maxExtents.x + stretchX;
		}

		if (stretchY < 0) {
			newMinY = source.minExtents.y + stretchY;
			newMaxY = source.maxExtents.y;
		} else {
			newMinY = source.minExtents.y;
			newMaxY = source.maxExtents.y + stretchY;
		}

		if (stretchZ < 0) {
			newMinZ = source.minExtents.z + stretchZ;
			newMaxZ = source.maxExtents.z;
		} else {
			newMinZ = source.minExtents.z;
			newMaxZ = source.maxExtents.z + stretchZ;
		}

		destination.minExtents.set(newMinX, newMinY, newMinZ);
		destination.maxExtents.set(newMaxX, newMaxY, newMaxZ);

		return destination;
	}

	/**
	 * Creates an AABB equivalent to this, but in a new position and rotation.
	 *
	 * @param source The source AABB.
	 * @param position The amount to move.
	 * @param rotation The amount to rotate.
	 * @param scale The amount to scale the object.
	 * @param destination The destination AABB or null if a new AABB is to be created.
	 *
	 * @return An AABB equivalent to this, but in a new position.
	 */
	public static AABB recalculate(AABB source, Vector3f position, Vector3f rotation, float scale, AABB destination) {
		if (destination == null) {
			destination = new AABB();
		}

		// Sets the destinations values to the sources.
		destination.minExtents.set(source.minExtents);
		destination.maxExtents.set(source.maxExtents);

		// Scales the dimensions for the AABB.
		destination.setMinExtents(destination.minExtents.x * scale, destination.minExtents.y * scale, destination.minExtents.z * scale);
		destination.setMaxExtents(destination.maxExtents.x * scale, destination.maxExtents.y * scale, destination.maxExtents.z * scale);

		// Creates the 8 AABB corners and rotates them.
		Vector3f fll = new Vector3f(destination.minExtents.x, destination.minExtents.y, destination.minExtents.z);
		Vector3f.rotate(fll, rotation, fll);

		Vector3f flr = new Vector3f(destination.maxExtents.x, destination.minExtents.y, destination.minExtents.z);
		Vector3f.rotate(flr, rotation, flr);

		Vector3f ful = new Vector3f(destination.minExtents.x, destination.maxExtents.y, destination.minExtents.z);
		Vector3f.rotate(ful, rotation, ful);

		Vector3f fur = new Vector3f(destination.maxExtents.x, destination.maxExtents.y, destination.minExtents.z);
		Vector3f.rotate(fur, rotation, fur);

		Vector3f bur = new Vector3f(destination.maxExtents.x, destination.maxExtents.y, destination.maxExtents.z);
		Vector3f.rotate(bur, rotation, bur);

		Vector3f bul = new Vector3f(destination.minExtents.x, destination.maxExtents.y, destination.maxExtents.z);
		Vector3f.rotate(bul, rotation, bul);

		Vector3f blr = new Vector3f(destination.maxExtents.x, destination.minExtents.y, destination.maxExtents.z);
		Vector3f.rotate(blr, rotation, blr);

		Vector3f bll = new Vector3f(destination.minExtents.x, destination.minExtents.y, destination.maxExtents.z);
		Vector3f.rotate(bll, rotation, bll);

		destination.minExtents = Maths.min(fll, Maths.min(flr, Maths.min(ful, Maths.min(fur, Maths.min(bur, Maths.min(bul, Maths.min(blr, bll)))))));
		destination.maxExtents = Maths.max(fll, Maths.max(flr, Maths.max(ful, Maths.max(fur, Maths.max(bur, Maths.max(bul, Maths.max(blr, bll)))))));

		// Transforms the AABB.
		Vector3f.add(destination.minExtents, position, destination.minExtents);
		Vector3f.add(destination.maxExtents, position, destination.maxExtents);

		// Returns the final AABB.
		return destination;
	}

	@Override
	public boolean contains(AABB other) {
		return minExtents.getX() <= other.minExtents.getX() &&
				other.maxExtents.getX() <= maxExtents.getX() &&
				minExtents.getY() <= other.minExtents.getY() &&
				other.maxExtents.getY() <= maxExtents.getY() &&
				minExtents.getZ() <= other.minExtents.getZ() &&
				other.maxExtents.getZ() <= maxExtents.getZ();
	}

	@Override
	public IntersectData intersects(AABB other) throws IllegalArgumentException {
		if (other == null) {
			throw new IllegalArgumentException("Null AABB collider.");
		} else if (equals(other)) {
			return new IntersectData(true, 0.0f);
		}

		float maxDist = Maths.max(Maths.max(new Vector3f(getMinExtents().getX() - other.getMaxExtents().getX(), getMinExtents().getY() - other.getMaxExtents().getY(), getMinExtents().getZ() - other.getMaxExtents().getZ()), new Vector3f(other.getMinExtents().getX() - getMaxExtents().getX(), other.getMinExtents().getY() - getMaxExtents().getY(), other.getMinExtents().getZ() - getMaxExtents().getZ())));
		return new IntersectData(maxDist < 0, maxDist);
	}

	/**
	 * Tests if a sphere intersects this AABB.
	 *
	 * @param sphere The sphere to test with.
	 *
	 * @return If the sphere intersects this AABB.
	 */
	public boolean intersectsSphere(Sphere sphere) {
		float distanceSquared = sphere.getRadius() * sphere.getRadius();

		if (sphere.getPosition().x < minExtents.x) {
			distanceSquared -= Math.pow(sphere.getPosition().x - minExtents.x, 2);
		} else if (sphere.getPosition().x > maxExtents.x) {
			distanceSquared -= Math.pow(sphere.getPosition().x - maxExtents.x, 2);
		}

		if (sphere.getPosition().y < minExtents.x) {
			distanceSquared -= Math.pow(sphere.getPosition().y - minExtents.y, 2);
		} else if (sphere.getPosition().x > maxExtents.x) {
			distanceSquared -= Math.pow(sphere.getPosition().y - maxExtents.y, 2);
		}

		if (sphere.getPosition().z < minExtents.x) {
			distanceSquared -= Math.pow(sphere.getPosition().z - minExtents.z, 2);
		} else if (sphere.getPosition().z > maxExtents.x) {
			distanceSquared -= Math.pow(sphere.getPosition().z - maxExtents.z, 2);
		}

		return distanceSquared > 0.0f;
	}

	@Override
	public boolean intersectsRay(Ray ray) {
		double tmin = (minExtents.x - ray.getOrigin().x) / ray.getCurrentRay().x;
		double tmax = (maxExtents.x - ray.getOrigin().x) / ray.getCurrentRay().x;

		if (tmin > tmax) {
			double temp = tmax;
			tmax = tmin;
			tmin = temp;
		}

		float tymin = (minExtents.y - ray.getOrigin().y) / ray.getCurrentRay().y;
		float tymax = (maxExtents.y - ray.getOrigin().y) / ray.getCurrentRay().y;

		if (tymin > tymax) {
			float temp = tymax;
			tymax = tymin;
			tymin = temp;
		}

		if ((tmin > tymax) || (tymin > tmax)) {
			return false;
		}

		if (tymin > tmin) {
			tmin = tymin;
		}

		if (tymax < tmax) {
			tmax = tymax;
		}

		float tzmin = (minExtents.z - ray.getOrigin().z) / ray.getCurrentRay().z;
		float tzmax = (maxExtents.z - ray.getOrigin().z) / ray.getCurrentRay().z;

		if (tzmin > tzmax) {
			float temp = tzmax;
			tzmax = tzmin;
			tzmin = temp;
		}

		if ((tmin > tzmax) || (tzmin > tmax)) {
			return false;
		}

		if (tzmin > tmin) {
			tmin = tzmin;
		}

		if (tzmax < tmax) {
			tmax = tzmax;
		}

		return true;
	}

	@Override
	public boolean contains(Vector3f point) {
		if (point.x > maxExtents.x) {
			return false;
		} else if (point.x < minExtents.x) {
			return false;
		} else if (point.y > maxExtents.y) {
			return false;
		} else if (point.y < minExtents.y) {
			return false;
		} else if (point.z > maxExtents.z) {
			return false;
		} else if (point.z < minExtents.z) {
			return false;
		}

		return true;
	}

	@Override
	public boolean inFrustum(Frustum frustum) {
		return frustum.cubeInFrustum(minExtents.x, minExtents.y, minExtents.z, maxExtents.x, maxExtents.y, maxExtents.z);
	}

	/**
	 * Calculates the centre of this AABB on the X axis.
	 *
	 * @return The centre location of this AABB on the X axis.
	 */
	public double getCentreX() {
		return (minExtents.getX() + maxExtents.getX()) / 2.0;
	}

	/**
	 * Calculates the centre of this AABB on the Y axis.
	 *
	 * @return The centre location of this AABB on the Y axis.
	 */
	public double getCentreY() {
		return (minExtents.getY() + maxExtents.getY()) / 2.0;
	}

	/**
	 * Calculates the centre of this AABB on the Z axis.
	 *
	 * @return The centre location of this AABB on the Z axis.
	 */
	public double getCentreZ() {
		return (minExtents.getZ() + maxExtents.getZ()) / 2.0;
	}

	/**
	 * Gets the width of this AABB.
	 *
	 * @return The width of this AABB.
	 */
	public double getWidth() {
		return maxExtents.getX() - minExtents.getX();
	}

	/**
	 * Gets the height of this AABB.
	 *
	 * @return The height of this AABB.
	 */
	public double getHeight() {
		return maxExtents.getY() - minExtents.getY();
	}

	/**
	 * Gets the depth of this AABB.
	 *
	 * @return The depth of this AABB.
	 */
	public double getDepth() {
		return maxExtents.getZ() - minExtents.getZ();
	}

	public Vector3f getMinExtents() {
		return minExtents;
	}

	public Vector3f getMaxExtents() {
		return maxExtents;
	}

	public void setMinExtents(Vector3f minExtents) {
		this.minExtents.set(minExtents);
	}

	public void setMinExtents(float minX, float minY, float minZ) {
		this.minExtents.set(minX, minY, minZ);
	}

	public void setMaxExtents(Vector3f maxExtents) {
		this.maxExtents.set(maxExtents);
	}

	public void setMaxExtents(float maxX, float maxY, float maxZ) {
		this.maxExtents.set(maxX, maxY, maxZ);
	}

	@Override
	public Model getRenderModel() {
		return Model.newModel(MODEL_FILE).create();
	}

	@Override
	public Vector3f getRenderCentre() {
		Vector3f position = Vector3f.add(getMaxExtents(), getMinExtents(), null);
		position.set(position.x / 2.0f, position.y / 2.0f, position.z / 2.0f);
		return position;
	}

	@Override
	public Vector3f getRenderScale() {
		Vector3f scale = Vector3f.subtract(getMaxExtents(), getMinExtents(), null);
		scale.set(scale.x / 2.0f, scale.y / 2.0f, scale.z / 2.0f);
		return scale;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 79 * hash + Objects.hashCode(minExtents);
		hash = 79 * hash + Objects.hashCode(maxExtents);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		} else if (super.getClass() != object.getClass()) {
			return false;
		}

		AABB that = (AABB) object;

		if (!Objects.equals(minExtents, that.minExtents)) {
			return false;
		} else if (!Objects.equals(maxExtents, that.maxExtents)) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return "AABB{" + "minExtents=" + minExtents + ", maxExtents=" + maxExtents + '}';
	}
}