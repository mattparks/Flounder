package com.flounder.particles.spawns;

import com.flounder.maths.*;
import com.flounder.maths.vectors.*;

public class SpawnSphere implements IParticleSpawn {
	private float radius;
	private Vector3f spawnPosition;

	public SpawnSphere(float radius) {
		this.radius = radius;
		this.spawnPosition = new Vector3f();
	}

	public SpawnSphere(String[] template) {
		this.radius = Float.parseFloat(template[0]);
		this.spawnPosition = new Vector3f();
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	@Override
	public Vector3f getBaseSpawnPosition() {
		Maths.generateRandomUnitVector(spawnPosition);

		spawnPosition.scale(radius);
		float a = Maths.RANDOM.nextFloat();
		float b = Maths.RANDOM.nextFloat();

		if (a > b) {
			float temp = a;
			a = b;
			b = temp;
		}

		float randX = (float) (b * Math.cos(2.0 * Math.PI * (a / b)));
		float randY = (float) (b * Math.sin(2.0 * Math.PI * (a / b)));
		float distance = new Vector2f(randX, randY).length();
		spawnPosition.scale(distance);
		return spawnPosition;
	}
}
