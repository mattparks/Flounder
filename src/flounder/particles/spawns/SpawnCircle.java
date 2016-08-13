package flounder.particles.spawns;

import flounder.maths.*;
import flounder.maths.vectors.*;

public class SpawnCircle implements IParticleSpawn {
	private Vector3f heading;
	private float radius;
	private Vector3f spawnPosition;

	public SpawnCircle(Vector3f heading, float radius) {
		this.heading = heading.normalize();
		this.radius = radius;
		this.spawnPosition = new Vector3f();
	}

	@Override
	public Vector3f getBaseSpawnPosition() {
		return Maths.randomPointOnCircle(spawnPosition, heading, radius);
	}
}
