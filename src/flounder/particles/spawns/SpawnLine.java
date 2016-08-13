package flounder.particles.spawns;

import flounder.maths.vectors.*;

import java.util.*;

public class SpawnLine implements IParticleSpawn {
	private float length;
	private Vector3f axis;
	private Random random;

	public SpawnLine(float length, Vector3f axis) {
		this.length = length;
		this.axis = axis.normalize();
		this.random = new Random();
	}

	public SpawnLine(String[] template) {
		this.length = Float.parseFloat(template[0]);
		this.axis = new Vector3f().set(template[1]).normalize();
		this.random = new Random();
	}

	public float getLength() {
		return length;
	}

	public void setLength(float length) {
		this.length = length;
	}

	public Vector3f getAxis() {
		return axis;
	}

	public void setAxis(Vector3f axis) {
		this.axis = axis;
	}

	@Override
	public String[] getSavableValues() {
		return new String[]{"" + length, axis.toString()};
	}

	@Override
	public Vector3f getBaseSpawnPosition() {
		Vector3f actualAxis = new Vector3f(axis.x * length, axis.y * length, axis.z * length);
		actualAxis.scale(random.nextFloat() - 0.5f);
		return actualAxis;
	}
}
