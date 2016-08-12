package flounder.physics;

import flounder.maths.vectors.*;

public class AABBMesh implements Collidable {
	private AABB[] mesh;

	public AABBMesh(AABB[] mesh) {
		this.mesh = mesh;

		for (AABB aabb : mesh) {
			aabb.setMaxExtents(aabb.getMaxExtents());
			aabb.setMinExtents(aabb.getMinExtents());
		}
	}

	public AABBMesh move(Vector3f position, Vector3f rotation, float scale) {
		AABB[] newMesh = new AABB[mesh.length];

		for (int i = 0; i < mesh.length; i++) {
			newMesh[i] = mesh[i].recalculate(mesh[i], position, rotation, scale);
		}

		return new AABBMesh(newMesh);
	}

	public IntersectData intersects(AABBMesh coll) {
		return this.intersects(coll.getMesh());
	}

	public IntersectData intersects(AABB[] coll) {
		if (coll == null) {
			return new IntersectData(false, 0);
		}

		boolean intersection = false;
		float minDist = Float.POSITIVE_INFINITY;

		for (AABB aabb : coll) {
			IntersectData data = this.intersects(aabb);

			if (data.getDistance() != Float.NEGATIVE_INFINITY) {
				if (!intersection) {
					intersection = data.isIntersection();
				}

				minDist = Math.min(minDist, data.getDistance());
			}
		}

		return new IntersectData(intersection, minDist);
	}

	public AABB[] getMesh() {
		return mesh;
	}

	public void setMesh(AABB[] AABBMesh) {
		mesh = AABBMesh;
	}

	@Override
	public AABB getAABB() {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public IntersectData intersects(AABB coll) {
		if (coll == null) {
			return new IntersectData(false, 0);
		}

		boolean intersection = false;
		float minDist = Float.POSITIVE_INFINITY;

		for (AABB aabb : mesh) {
			IntersectData data = aabb.intersects(coll);

			if (data.getDistance() != Float.NEGATIVE_INFINITY) {
				if (!intersection) {
					intersection = data.isIntersection();
				}

				minDist = Math.min(minDist, data.getDistance());
			}
		}

		return new IntersectData(intersection, minDist);
	}

	@Override
	public ColliderRadial getRadialCollider() {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public IntersectData intersects(ColliderRadial coll) {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public IntersectData intersects(Collidable coll) {
		throw new UnsupportedOperationException("Not supported.");
	}
}