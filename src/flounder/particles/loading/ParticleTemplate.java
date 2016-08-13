package flounder.particles.loading;

import flounder.textures.*;

/**
 * A definition for what a particle should act and look like.
 */
public class ParticleTemplate {
	private String name;
	private Texture texture;
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

	public void setName(String name) {
		this.name = name;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	public void setGravityEffect(float gravityEffect) {
		this.gravityEffect = gravityEffect;
	}

	public void setLifeLength(float lifeLength) {
		this.lifeLength = lifeLength;
	}

	public void setScale(float scale) {
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
