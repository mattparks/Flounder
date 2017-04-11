package flounder.particles;

import flounder.camera.*;
import flounder.framework.*;
import flounder.maths.vectors.*;
import flounder.physics.*;
import flounder.space.*;

/**
 * A instance of a particle type.
 */
public class Particle implements ISpatialObject, Comparable<Particle> {
	private ParticleType particleType;

	private Vector3f position;
	private Vector3f velocity;
	private Vector3f change;
	private AABB aabb;

	private Vector2f textureOffset1;
	private Vector2f textureOffset2;

	private float lifeLength;
	private float rotation;
	private float scale;
	private float gravityEffect;

	private float elapsedTime;
	private float transparency;
	private float textureBlendFactor;
	private float distanceToCamera;

	/**
	 * Creates a new particle object.
	 *
	 * @param particleType The particle template to build from.
	 * @param position The particles initial position.
	 * @param velocity The particles initial velocity.
	 * @param lifeLength The particles life length.
	 * @param rotation The particles rotation.
	 * @param scale The particles scale.
	 * @param gravityEffect The particles gravity effect.
	 */
	protected Particle(ParticleType particleType, Vector3f position, Vector3f velocity, float lifeLength, float rotation, float scale, float gravityEffect) {
		set(particleType, position, velocity, lifeLength, rotation, scale, gravityEffect);
	}

	/**
	 * Sets this particle to a new particle object.
	 *
	 * @param particleType The particle template to build from.
	 * @param position The particles initial position.
	 * @param velocity The particles initial velocity.
	 * @param lifeLength The particles life length.
	 * @param rotation The particles rotation.
	 * @param scale The particles scale.
	 * @param gravityEffect The particles gravity effect.
	 *
	 * @return this.
	 */
	protected Particle set(ParticleType particleType, Vector3f position, Vector3f velocity, float lifeLength, float rotation, float scale, float gravityEffect) {
		this.particleType = particleType;
		this.position = position;
		this.velocity = velocity;
		this.change = new Vector3f();
		this.aabb = new AABB();

		this.textureOffset1 = new Vector2f();
		this.textureOffset2 = new Vector2f();

		this.lifeLength = lifeLength;
		this.rotation = rotation;
		this.scale = scale;
		this.gravityEffect = gravityEffect;

		this.elapsedTime = 0.0f;
		this.transparency = 0.0f;
		this.textureBlendFactor = 0.0f;
		this.distanceToCamera = 0.0f;

		return this;
	}

	/**
	 * Updates the particle.
	 */
	protected void update() {
		velocity.y += -10.0f * gravityEffect * Framework.getDelta();
		change.set(velocity);
		change.scale(Framework.getDelta());

		Vector3f.add(change, position, position);
		elapsedTime += Framework.getDelta();

		if (elapsedTime > lifeLength) {
			transparency += 1.0f * Framework.getDelta();
		}

		if (!isAlive() || FlounderCamera.getCamera() == null) {
			return;
		}

		distanceToCamera = Vector3f.subtract(FlounderCamera.getCamera().getPosition(), position, null).lengthSquared();

		float size = 0.5f * particleType.getScale();
		aabb.getMinExtents().set(position.getX() - size, position.getY() - size, position.getZ() - size);
		aabb.getMaxExtents().set(position.getX() + size, position.getY() + size, position.getZ() + size);

		float lifeFactor = elapsedTime / lifeLength;

		if (particleType.getTexture() == null) {
			return;
		}

		int stageCount = (int) Math.pow(particleType.getTexture().getNumberOfRows(), 2);
		float atlasProgression = lifeFactor * stageCount;
		int index1 = (int) Math.floor(atlasProgression);
		int index2 = index1 < stageCount - 1 ? index1 + 1 : index1;

		this.textureBlendFactor = atlasProgression % 1.0f;
		updateTextureOffset(this.textureOffset1, index1);
		updateTextureOffset(this.textureOffset2, index2);
	}

	private Vector2f updateTextureOffset(Vector2f offset, int index) {
		offset.set(0.0f, 0.0f);
		int column = index % particleType.getTexture().getNumberOfRows();
		int row = index / particleType.getTexture().getNumberOfRows();
		offset.x = (float) column / particleType.getTexture().getNumberOfRows();
		offset.y = (float) row / particleType.getTexture().getNumberOfRows();
		return offset;
	}

	protected ParticleType getParticleType() {
		return particleType;
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

	public float getRotation() {
		return rotation;
	}

	public float getScale() {
		return scale;
	}

	protected float getTextureBlendFactor() {
		return textureBlendFactor;
	}

	protected float getElapsedTime() {
		return elapsedTime;
	}

	protected float getDistance() {
		return distanceToCamera;
	}

	@Override
	public AABB getCollider() {
		return aabb;
	}

	@Override
	public int compareTo(Particle o) {
		if (!isAlive()) {
			return ((Float) elapsedTime).compareTo(o.elapsedTime);
		} else {
			return ((Float) distanceToCamera).compareTo(o.distanceToCamera);
		}
	}
}
