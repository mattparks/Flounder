package flounder.particles.spawns;

import flounder.maths.vectors.*;

import javax.swing.*;

public class SpawnPoint implements IParticleSpawn {
	private Vector3f point;

	public SpawnPoint() {
		point = new Vector3f();
	}

	public SpawnPoint(String[] template) {
		point = new Vector3f();
	}

	@Override
	public String[] getSavableValues() {
		return new String[]{};
	}

	@Override
	public Vector3f getBaseSpawnPosition() {
		return point;
	}

	@Override
	public void addToPanel(JPanel panel) {

	}
}
