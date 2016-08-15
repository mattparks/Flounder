package flounder.particles.spawns;

import flounder.maths.vectors.*;

import javax.swing.*;

/**
 * A interface that defines a particle spawn type.
 */
public interface IParticleSpawn {
	/**
	 * Gets the base spawn position.
	 *
	 * @return The base spawn position.
	 */
	Vector3f getBaseSpawnPosition();

	/**
	 * Gets a list of saveable values.
	 *
	 * @return The saveable values.
	 */
	String[] getSavableValues();

	void addToPanel(JPanel panel);
}
