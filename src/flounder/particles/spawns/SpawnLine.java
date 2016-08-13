package flounder.particles.spawns;

import flounder.maths.vectors.*;

import java.util.*;

public class SpawnLine implements IParticleSpawn {
	private float length;
	private Vector3f axis;
	private Random random = new Random();

	public SpawnLine(float length, Vector3f axis) {
		this.length = length;
		this.axis = axis.normalize();
	}

	public Vector3f getBaseSpawnPosition() {
		Vector3f actualAxis = new Vector3f(axis.x * length, axis.y * length, axis.z * length);
		actualAxis.scale(random.nextFloat() - 0.5f);
		return actualAxis;
	}
}
