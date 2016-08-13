package flounder.particles;

import flounder.textures.*;

/**
 * A definition for what a particle should act and look like.
 */
public class ParticleType {
	private final Texture texture;
	private float gravityEffect;
	private float lifeLength;
	private float scale;

	/**
	 * Creates a new particle type.
	 *
	 * @param texture The particles texture.
	 * @param gravityEffect How much gravity will effect the particle.
	 * @param lifeLength The averaged life length for the particle.
	 * @param scale The averaged scale for the particle.
	 */
	public ParticleType(Texture texture, float gravityEffect, float lifeLength, float scale) {
		this.texture = texture;
		this.gravityEffect = gravityEffect;
		this.lifeLength = lifeLength;
		this.scale = scale;
	}

	protected Texture getTexture() {
		return texture;
	}

	protected float getGravityEffect() {
		return gravityEffect;
	}

	protected float getLifeLength() {
		return lifeLength;
	}

	protected float getScale() {
		return scale;
	}
}
