package flounder.particles.spawns;

import flounder.maths.vectors.*;

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
}
