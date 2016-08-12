package flounder.physics;

import flounder.maths.vectors.*;

import java.util.*;

/**
 * A 3D axis-aligned bounding box.
 */
public class AABB {
	private Vector3f minExtents;
	private Vector3f maxExtents;

	private Vector3f rotation;

	/**
	 * Creates a new blank 3D AABB.
	 */
	public AABB() {
		this(new Vector3f(), new Vector3f());
	}

	/**
	 * Creates a new mixed AABB based on it's extents.
	 *
	 * @param minExtents The minimum extent of the box.
	 * @param maxExtents The minimum extent of the box.
	 */
	public AABB(Vector3f minExtents, Vector3f maxExtents) {
		this.minExtents = minExtents;
		this.maxExtents = maxExtents;
		this.rotation = new Vector3f();
	}

	/**
	 * Creates a new AABB from another AABB source.
	 *
	 * @param source The source to create off of.
	 */
	public AABB(AABB source) {
		this(new Vector3f(source.getMinExtents()), new Vector3f(source.getMaxExtents()));
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

	public void setMinExtents(Vector3f minExtents) {
		this.minExtents.set(minExtents);
	}

	public Vector3f getMaxExtents() {
		return maxExtents;
	}

	public void setMaxExtents(Vector3f maxExtents) {
		this.maxExtents.set(maxExtents);
	}

	public void setMinExtents(float minX, float minY, float minZ) {
		this.minExtents.set(minX, minY, minZ);
	}

	public void setMaxExtents(float maxX, float maxY, float maxZ) {
		this.maxExtents.set(maxX, maxY, maxZ);
	}

	public Vector3f getRotation() {
		return rotation;
	}

	/**
	 * Creates a new AABB equivalent to this, scaled away from the centre origin.
	 *
	 * @param scale Amount to scale up the AABB.
	 *
	 * @return A new AABB, scaled by the specified amounts.
	 */
	public AABB scale(AABB destination, Vector3f scale) {
		return scale(destination, scale.x, scale.y, scale.z);
	}

	/**
	 * Creates a new AABB equivalent to this, scaled away from the centre origin.
	 *
	 * @param scaleX Amount to scale up the AABB on X.
	 * @param scaleY Amount to scale up the AABB on Y.
	 * @param scaleZ Amount to scale up the AABB on Z.
	 *
	 * @return A new AABB, scaled by the specified amounts.
	 */
	public AABB scale(AABB destination, float scaleX, float scaleY, float scaleZ) {
		if (destination == null) {
			destination = new AABB();
		}

		destination.setMinExtents(minExtents.x * scaleX, minExtents.y * scaleY, minExtents.z * scaleZ);
		destination.setMaxExtents(maxExtents.x * scaleX, maxExtents.y * scaleY, maxExtents.z * scaleZ);
		return destination;
	}

	/**
	 * Creates a new AABB equivalent to this, but scaled away from the origin by a certain amount.
	 *
	 * @param expand Amount to scale up the AABB.
	 *
	 * @return A new AABB, scaled by the specified amounts.
	 */
	public AABB expand(Vector3f expand) {
		return expand(expand.x, expand.y, expand.z);
	}

	/**
	 * Creates a new AABB equivalent to this, but scaled away from the origin by a certain amount.
	 *
	 * @param expandX Amount to scale up the AABB on X.
	 * @param expandY Amount to scale up the AABB on Y.
	 * @param expandZ Amount to scale up the AABB on Z.
	 *
	 * @return A new AABB, scaled by the specified amounts.
	 */
	public AABB expand(float expandX, float expandY, float expandZ) {
		return new AABB(new Vector3f(minExtents.x - expandX, minExtents.y - expandY, minExtents.z - expandZ), new Vector3f(maxExtents.x + expandX, maxExtents.y + expandY, maxExtents.z + expandZ));
	}

	/**
	 * Creates an AABB that bounds both this AABB and another AABB.
	 *
	 * @param other The other AABB being bounded.
	 *
	 * @return An AABB that bounds both this AABB and {@code other}.
	 */
	public AABB combine(AABB other) {
		float newMinX = Math.min(minExtents.x, other.getMinExtents().x);
		float newMinY = Math.min(minExtents.y, other.getMinExtents().y);
		float newMinZ = Math.min(minExtents.z, other.getMinExtents().z);
		float newMaxX = Math.max(maxExtents.x, other.getMaxExtents().x);
		float newMaxY = Math.max(maxExtents.y, other.getMaxExtents().y);
		float newMaxZ = Math.max(maxExtents.z, other.getMaxExtents().z);
		return new AABB(new Vector3f(newMinX, newMinY, newMinZ), new Vector3f(newMaxX, newMaxY, newMaxZ));
	}

	/**
	 * Creates a new AABB equivalent to this, but stretched by a certain amount.
	 *
	 * @param stretch The amount to stretch.
	 *
	 * @return A new AABB, stretched by the specified amounts.
	 */
	public AABB stretch(Vector3f stretch) {
		return stretch(stretch.x, stretch.y, stretch.z);
	}

	/**
	 * Creates a new AABB equivalent to this, but stretched by a certain amount.
	 *
	 * @param stretchX The amount to stretch on the X.
	 * @param stretchY The amount to stretch on the Y.
	 * @param stretchZ The amount to stretch on the Z.
	 *
	 * @return A new AABB, stretched by the specified amounts.
	 */
	public AABB stretch(float stretchX, float stretchY, float stretchZ) {
		float newMinX, newMaxX, newMinY, newMaxY, newMinZ, newMaxZ;

		if (stretchX < 0) {
			newMinX = minExtents.x + stretchX;
			newMaxX = maxExtents.x;
		} else {
			newMinX = minExtents.x;
			newMaxX = maxExtents.x + stretchX;
		}

		if (stretchY < 0) {
			newMinY = minExtents.y + stretchY;
			newMaxY = maxExtents.y;
		} else {
			newMinY = minExtents.y;
			newMaxY = maxExtents.y + stretchY;
		}

		if (stretchZ < 0) {
			newMinZ = minExtents.z + stretchZ;
			newMaxZ = maxExtents.z;
		} else {
			newMinZ = minExtents.z;
			newMaxZ = maxExtents.z + stretchZ;
		}

		return new AABB(new Vector3f(newMinX, newMinY, newMinZ), new Vector3f(newMaxX, newMaxY, newMaxZ));
	}

	/**
	 * Creates an AABB equivalent to this, but in a new position and rotation.
	 *
	 * @param position The amount to move.
	 * @param rotation The amount to rotate.
	 *
	 * @return An AABB equivalent to this, but in a new position.
	 */
	public AABB recalculate(AABB destination, Vector3f position, Vector3f rotation, float scale) {
		if (destination == null) {
			destination = new AABB();
		}

		destination.setMinExtents(minExtents.x * scale, minExtents.y * scale, minExtents.z * scale);
		destination.setMaxExtents(maxExtents.x * scale, maxExtents.y * scale, maxExtents.z * scale);
		Vector3f.add(destination.getMinExtents(), position, destination.getMinExtents());
		Vector3f.add(destination.getMaxExtents(), position, destination.getMaxExtents());

		// TODO: Fix rotate vector! That math is broken proven with: http://www.nh.cas.cz/people/lazar/celler/online_tools.php
		// Vector3f.rotateVector(minExtents, rotation.x, rotation.y, rotation.z, null)
		// Vector3f.rotateVector(maxExtents, rotation.x, rotation.y, rotation.z, null)
		return destination;
	}

	/**
	 * Tests whether another AABB is completely contained by this one.
	 *
	 * @param other The AABB being tested for containment
	 *
	 * @return True if {@code other} is contained by this AABB, false otherwise.
	 */
	public boolean contains(AABB other) {
		return minExtents.getX() <= other.minExtents.getX() && other.maxExtents.getX() <= maxExtents.getX() && minExtents.getY() <= other.minExtents.getY() && other.maxExtents.getY() <= maxExtents.getY() && minExtents.getZ() <= other.minExtents.getZ() && other.maxExtents.getZ() <= maxExtents.getZ();
	}

	/**
	 * Tests whether another AABB is intersecting this one.
	 *
	 * @param other The AABB being tested for intersection
	 *
	 * @return True if {@code other} is intersecting this AABB, false otherwise.
	 */
	public IntersectData intersects(AABB other) throws IllegalArgumentException {
		if (other == null) {
			throw new IllegalArgumentException("Null AABB collider.");
		} else if (equals(other)) {
			return new IntersectData(true, 0);
		}

		float maxDist = Vector3f.maxComponent(Vector3f.maxVector(new Vector3f(getMinExtents().x - other.getMaxExtents().x, getMinExtents().y - other.getMaxExtents().y, getMinExtents().z - other.getMaxExtents().z), new Vector3f(other.getMinExtents().x - getMaxExtents().x, other.getMinExtents().y - getMaxExtents().y, other.getMinExtents().z - getMaxExtents().z)));
		return new IntersectData(maxDist < 0, maxDist);
	}

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