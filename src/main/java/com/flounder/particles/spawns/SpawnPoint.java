package com.flounder.particles.spawns;

import com.flounder.maths.vectors.*;

public class SpawnPoint implements IParticleSpawn {
	private Vector3f point;

	public SpawnPoint() {
		point = new Vector3f();
	}

	public SpawnPoint(String[] template) {
		point = new Vector3f();
	}

	@Override
	public Vector3f getBaseSpawnPosition() {
		return point;
	}
}
