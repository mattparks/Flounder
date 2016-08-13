package flounder.particles.spawns;

import flounder.maths.vectors.*;

public class SpawnPoint implements IParticleSpawn {
	private Vector3f point;

	public SpawnPoint() {
		point = new Vector3f();
	}

	public Vector3f getPoint() {
		return point;
	}

	public void setPoint(Vector3f point) {
		this.point = point;
	}

	@Override
	public Vector3f getBaseSpawnPosition() {
		return point;
	}
}
