package flounder.particles.spawns;

import flounder.maths.*;
import flounder.maths.vectors.*;

public class SpawnSphere implements IParticleSpawn {
	private float radius;

	public SpawnSphere(float radius) {
		this.radius = radius;
	}

	public Vector3f getBaseSpawnPosition() {
		Vector3f spherePoint = Maths.generateRandomUnitVector(null);

		spherePoint.scale(this.radius);
		float a = Maths.RANDOM.nextFloat();
		float b = Maths.RANDOM.nextFloat();

		if (a > b) {
			float temp = a;
			a = b;
			b = temp;
		}

		float randX = (float) (b * Math.cos(6.283185307179586 * (a / b)));
		float randY = (float) (b * Math.sin(6.283185307179586 * (a / b)));
		float distance = new Vector2f(randX, randY).length();
		spherePoint.scale(distance);
		return spherePoint;
	}
}
