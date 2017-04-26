package flounder.entities.components.particles;

import flounder.particles.spawns.*;

import javax.swing.*;

public class EditorParticlePoint extends IEditorParticleSpawn {
	private SpawnPoint spawn;

	public EditorParticlePoint() {
		spawn = new SpawnPoint();
	}

	@Override
	public String getTabName() {
		return "Point";
	}

	@Override
	public SpawnPoint getComponent() {
		return spawn;
	}

	@Override
	public void addToPanel(JPanel panel) {
	}

	@Override
	public String[] getSavableValues() {
		return new String[]{};
	}
}
