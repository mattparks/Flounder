package flounder.particles;

import flounder.engine.*;
import flounder.maths.vectors.*;
import flounder.particles.loading.*;

/**
 * A instance of a particle type.
 */
public class Particle implements Comparable<Particle> {
	private final ParticleTemplate particleTemplate;

	private final Vector3f position;
	private final Vector3f velocity;
	private final Vector3f reusableChange;

	private final Vector2f textureOffset1;
	private final Vector2f textureOffset2;

	private boolean visable;

	private float lifeLength;
	private float rotation;
	private float scale;

	private float elapsedTime;
	private float transparency;
	private float textureBlendFactor;
	private float distanceToCamera;

	protected Particle(final ParticleTemplate particleTemplate, final Vector3f position, final Vector3f velocity, float lifeLength, float rotation, float scale) {
		this.particleTemplate = particleTemplate;
		this.position = position;
		this.velocity = velocity;
		this.reusableChange = new Vector3f();

		this.textureOffset1 = new Vector2f();
		this.textureOffset2 = new Vector2f();

		this.visable = true;

		this.lifeLength = lifeLength;
		this.rotation = rotation;
		this.scale = scale;

		this.elapsedTime = 0.0f;
		this.transparency = 0.0f;
		this.textureBlendFactor = 0.0f;
		this.distanceToCamera = 0.0f;
		FlounderEngine.getParticles().addParticle(this);
	}

	protected void update(final boolean moveParticle) {
		if (moveParticle) {
			velocity.y += -10.0f * particleTemplate.getGravityEffect() * FlounderEngine.getDelta();
			reusableChange.set(velocity);
			reusableChange.scale(FlounderEngine.getDelta());

			Vector3f.add(reusableChange, position, position);
			elapsedTime += FlounderEngine.getDelta();

			if (elapsedTime > lifeLength) {
				transparency += 1.0f * FlounderEngine.getDelta();
			}
		}

		distanceToCamera = Vector3f.subtract(FlounderEngine.getCamera().getPosition(), position, null).lengthSquared();

		float lifeFactor = elapsedTime / lifeLength;
		int stageCount = (int) Math.pow(particleTemplate.getTexture().getNumberOfRows(), 2);
		float atlasProgression = lifeFactor * stageCount;
		int index1 = (int) Math.floor(atlasProgression);
		int index2 = index1 < stageCount - 1 ? index1 + 1 : index1;

		this.textureBlendFactor = atlasProgression % 1.0f;
		updateTextureOffset(this.textureOffset1, index1);
		updateTextureOffset(this.textureOffset2, index2);
	}

	private Vector2f updateTextureOffset(final Vector2f offset, final int index) {
		offset.set(0.0f, 0.0f);
		int column = index % particleTemplate.getTexture().getNumberOfRows();
		int row = index / particleTemplate.getTexture().getNumberOfRows();
		offset.x = (float) column / particleTemplate.getTexture().getNumberOfRows();
		offset.y = (float) row / particleTemplate.getTexture().getNumberOfRows();
		return offset;
	}

	protected ParticleTemplate getParticleTemplate() {
		return particleTemplate;
	}

	protected Vector3f getPosition() {
		return position;
	}

	protected float getTransparency() {
		return transparency;
	}

	protected boolean isAlive() {
		return transparency < 1.0;
	}

	protected Vector2f getTextureOffset1() {
		return textureOffset1;
	}

	protected Vector2f getTextureOffset2() {
		return textureOffset2;
	}

	protected boolean isVisable() {
		return visable;
	}

	protected void setVisable(final boolean visable) {
		this.visable = visable;
	}

	public float getRotation() {
		return rotation;
	}

	public float getScale() {
		return scale;
	}

	protected float getTextureBlendFactor() {
		return textureBlendFactor;
	}

	protected float getDistance() {
		return distanceToCamera;
	}

	@Override
	public int compareTo(final Particle other) {
		return ((Float) distanceToCamera).compareTo(other.getDistance());
	}
}
