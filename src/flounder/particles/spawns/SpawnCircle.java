package flounder.particles.spawns;

import flounder.maths.*;
import flounder.maths.vectors.*;

public class SpawnCircle implements IParticleSpawn {
	private float radius;
	private Vector3f heading;
	private Vector3f spawnPosition;

	public SpawnCircle(float radius, Vector3f heading) {
		this.radius = radius;
		this.heading = heading.normalize();
		this.spawnPosition = new Vector3f();
	}

	public SpawnCircle(String[] template) {
		this.radius = Float.parseFloat(template[2]);
		this.heading = IParticleSpawn.createVector3f(template[1]).normalize();
		this.spawnPosition = new Vector3f();
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public Vector3f getHeading() {
		return heading;
	}

	public void setHeading(Vector3f heading) {
		this.heading = heading;
	}

	@Override
	public Vector3f getBaseSpawnPosition() {
		return Maths.randomPointOnCircle(spawnPosition, heading, radius);
	}
}
