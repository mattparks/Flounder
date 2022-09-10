package com.flounder.particles;

import com.flounder.textures.*;

/**
 * A definition for what a particle should act and look like.
 */
public class ParticleType {
	private String name;
	private TextureObject texture;
	private float lifeLength;
	private float scale;

	/**
	 * Creates a new particle type.
	 *
	 * @param name The name for the particle type.
	 * @param texture The particles texture.
	 * @param lifeLength The averaged life length for the particle.
	 * @param scale The averaged scale for the particle.
	 */
	public ParticleType(String name, TextureObject texture, float lifeLength, float scale) {
		this.name = name;
		this.texture = texture;
		this.lifeLength = lifeLength;
		this.scale = scale;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTexture(TextureObject texture) {
		this.texture = texture;
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

	public TextureObject getTexture() {
		return texture;
	}

	public float getLifeLength() {
		return lifeLength;
	}

	public float getScale() {
		return scale;
	}
}
