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

	public SpawnCircle(String[] template) {
		this.heading = new Vector3f().set(template[0]).normalize();
		this.radius = Float.parseFloat(template[1]);
		this.spawnPosition = new Vector3f();
	}

	public Vector3f getHeading() {
		return heading;
	}

	public void setHeading(Vector3f heading) {
		this.heading = heading;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	@Override
	public String[] getSavableValues() {
		return new String[]{heading.toString(), "" + radius};
	}

	@Override
	public Vector3f getBaseSpawnPosition() {
		return Maths.randomPointOnCircle(spawnPosition, heading, radius);
	}
}
