package flounder.physics;

public class IntersectData {
	private float distance;
	private boolean intersection;

	public IntersectData(boolean intersects, float distance) {
		this.distance = distance;
		intersection = intersects;
	}

	public float getDistance() {
		return distance;
	}

	public boolean isIntersection() {
		return intersection;
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
