package flounder.particles.loading;

import flounder.textures.*;

/**
 * A definition for what a particle should act and look like.
 */
public class ParticleTemplate {
	private final String name;
	private final Texture texture;
	private float gravityEffect;
	private float lifeLength;
	private float scale;

	/**
	 * Creates a new particle type.
	 *
	 * @param name The name for the particle type.
	 * @param texture The particles texture.
	 * @param gravityEffect How much gravity will effect the particle.
	 * @param lifeLength The averaged life length for the particle.
	 * @param scale The averaged scale for the particle.
	 */
	public ParticleTemplate(String name, Texture texture, float gravityEffect, float lifeLength, float scale) {
		this.name = name;
		this.texture = texture;
		this.gravityEffect = gravityEffect;
		this.lifeLength = lifeLength;
		this.scale = scale;
	}

	public String getName() {
		return name;
	}

	public Texture getTexture() {
		return texture;
	}

	public float getGravityEffect() {
		return gravityEffect;
	}

	public float getLifeLength() {
		return lifeLength;
	}

	public float getScale() {
		return scale;
	}
}
