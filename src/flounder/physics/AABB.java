package flounder.physics;

import flounder.maths.vectors.*;

import java.util.*;

public class AABB {
	private Vector3f minExtents;
	private Vector3f maxExtents;

	public AABB() {
		this(new Vector3f(), new Vector3f());
	}

	public AABB(Vector3f minExtents, Vector3f maxExtents) {
		this.minExtents = minExtents;
		this.maxExtents = maxExtents;
	}

	public AABB(AABB source) {
		this(new Vector3f(source.getMinExtents()), new Vector3f(source.getMaxExtents()));
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

	/**
	 * Creates a new AABB equivalent to this, scaled away from the center origin.
	 *
	 * @param scale Amount to scale up the AABB.
	 *
	 * @return A new AABB, scaled by the specified amounts.
	 */
	public AABB scale(Vector3f scale) {
		return scale(scale.x, scale.y, scale.z);
	}

	/**
	 * Creates a new AABB equivalent to this, scaled away from the center origin.
	 *
	 * @param scaleX Amount to scale up the AABB on X.
	 * @param scaleY Amount to scale up the AABB on Y.
	 * @param scaleZ Amount to scale up the AABB on Z.
	 *
	 * @return A new AABB, scaled by the specified amounts.
	 */
	public AABB scale(float scaleX, float scaleY, float scaleZ) {
		return new AABB(new Vector3f(minExtents.x * scaleX, minExtents.y * scaleY, minExtents.z * scaleZ), new Vector3f(maxExtents.x * scaleX, maxExtents.y * scaleY, maxExtents.z * scaleZ));
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
	public AABB recalculate(Vector3f position, Vector3f rotation) {
		// TODO: Fix rotate vector! That math is broken proven with: http://www.nh.cas.cz/people/lazar/celler/online_tools.php
		// Vector3f.rotateVector(minExtents, rotation.x, rotation.y, rotation.z, null)
		// Vector3f.rotateVector(maxExtents, rotation.x, rotation.y, rotation.z, null)
		return new AABB(Vector3f.add(minExtents, position, null), Vector3f.add(maxExtents, position, null));
	}

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