package flounder.particles.spawns;

import flounder.maths.*;
import flounder.maths.vectors.*;

public class SpawnCone implements IParticleSpawn {
	private Vector3f coneDirection;
	private float angle;
	private Vector3f spawnPosition;

	public SpawnCone(Vector3f coneDirection, float angle) {
		this.coneDirection = coneDirection;
		this.angle = angle;
		this.spawnPosition = new Vector3f();
	}

	public Vector3f getConeDirection() {
		return coneDirection;
	}

	public void setConeDirection(Vector3f coneDirection) {
		this.coneDirection = coneDirection;
	}

	public float getAngle() {
		return angle;
	}

	public void setAngle(float angle) {
		this.angle = angle;
	}

	@Override
	public Vector3f getBaseSpawnPosition() {
		return Maths.generateRandomUnitVectorWithinCone(spawnPosition, coneDirection, angle);
	}
}
