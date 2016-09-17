package flounder.physics;

/**
 * A class that represents collision data.
 */
public class IntersectData {
	private boolean intersection;
	private float distance;

	/**
	 * Creates a new collision data.
	 *
	 * @param intersects If there is a collision.
	 * @param distance What distance that collision is at.
	 */
	public IntersectData(boolean intersects, float distance) {
		this.intersection = intersects;
		this.distance = distance;
	}

	/**
	 * Gets if there is a collision.
	 *
	 * @return If there is a collision.
	 */
	public boolean isIntersection() {
		return intersection;
	}

	/**
	 * Gets the distance that collision is at.
	 *
	 * @return The distance that collision is at.
	 */
	public float getDistance() {
		return distance;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 89 * hash + Float.floatToIntBits(distance);
		hash = 89 * hash + (intersection ? 1 : 0);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (super.getClass() != obj.getClass()) {
			return false;
		}

		IntersectData that = (IntersectData) obj;

		if (Float.floatToIntBits(distance) != Float.floatToIntBits(that.distance)) {
			return false;
		} else if (intersection != that.intersection) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return "IntersectData{" + "distance=" + distance + ", intersection=" + intersection + "}";
	}
}
