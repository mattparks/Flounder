package flounder.physics;

import flounder.maths.vectors.*;

public class ColliderPlane {
	private float distance;
	private Vector3f normal;

	public ColliderPlane(Vector3f normal, float distance) {
		this.distance = distance;
		this.normal = normal;
	}

	public ColliderPlane normalized() {
		return new ColliderPlane(new Vector3f(normal.getX() / normal.length(), normal.getY() / normal.length(), normal.getZ() / normal.length()), distance / normal.length());
	}

	public IntersectData intersect(ColliderRadial coll) {
		float distFromColl = Math.abs(Vector3f.dot(getNormal(), coll.getPosition()) + distance) - coll.getRadius();
		return new IntersectData(distFromColl < 0, distFromColl);
	}

	public Vector3f getNormal() {
		return normal;
	}

	public float getDistance() {
		return distance;
	}
}
