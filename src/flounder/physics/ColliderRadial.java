package flounder.physics;

import flounder.maths.vectors.*;

import java.util.*;

public class ColliderRadial {
	private Vector3f position;
	private float radiusSq;

	public ColliderRadial() {
		this(0.0f, null);
	}

	public ColliderRadial(float radiusSq, Vector3f position) {
		this.radiusSq = radiusSq;
		this.position = position;
	}

	public IntersectData intersects(ColliderRadial coll) {
		if (coll == null) {
			return new IntersectData(false, 0);
		} else if (equals(coll)) {
			return new IntersectData(true, 0);
		}

		float distSq = Vector3f.getDistanceSquared(getPosition(), coll.getPosition());
		return new IntersectData(distSq <= Math.min(getRadiusSq(), coll.getRadiusSq()), (float) java.lang.Math.sqrt(distSq));
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position.set(position);
	}

	public float getRadiusSq() {
		return radiusSq;
	}

	public void setRadiusSq(float radiusSq) {
		this.radiusSq = radiusSq;
	}

	public float getRadius() {
		return (float) Math.pow(radiusSq, 2);
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 37 * hash + Float.floatToIntBits(radiusSq);
		hash = 37 * hash + Objects.hashCode(position);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		} else if (super.getClass() != object.getClass()) {
			return false;
		}

		ColliderRadial other = (ColliderRadial) object;

		if (Float.floatToIntBits(radiusSq) != Float.floatToIntBits(other.radiusSq)) {
			return false;
		} else if (!Objects.equals(position, other.position)) {
			return false;
		}

		return true;
	}
}
