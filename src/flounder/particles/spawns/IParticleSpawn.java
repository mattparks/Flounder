package flounder.particles.spawns;

import flounder.maths.vectors.*;

/**
 * A interface that defines a particle spawn type.
 */
@FunctionalInterface
public interface IParticleSpawn {
	/**
	 * Gets the base spawn position.
	 *
	 * @return The base spawn position.
	 */
	Vector3f getBaseSpawnPosition();

	public static Vector3f createVector3f(String source) {
		String reduced = source.replace("Vector3f(", "").replace(")", "").trim();
		String[] split = reduced.split("\\|");
		float x = Float.parseFloat(split[0].substring(2, split[0].length()));
		float y = Float.parseFloat(split[1].substring(2, split[0].length()));
		float z = Float.parseFloat(split[2].substring(2, split[0].length()));
		return new Vector3f(x, y, z);
	}
}
