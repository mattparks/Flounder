package flounder.particles;

import flounder.engine.*;
import flounder.maths.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;
import flounder.particles.loading.*;
import flounder.particles.spawns.*;

import java.util.*;

/**
 * A system of particles that are to be spawned.
 */
public class ParticleSystem {
	private List<ParticleTemplate> types;
	private IParticleSpawn spawn;
	private float pps;
	private float averageSpeed;
	private float gravityEffect;
	private boolean randomRotation;
	private Random random;

	private Vector3f systemCentre;
	private Vector3f centreVelocity;

	private Vector3f direction;
	private float directionDeviation;
	private float speedError;
	private float lifeError;
	private float scaleError;

	private boolean paused;

	/**
	 * Creates a new particle system.
	 *
	 * @param types The types of particles to spawn.
	 * @param spawn The particle spawn types.
	 * @param pps Particles per second.
	 * @param speed The particle speed.
	 * @param gravityEffect How much gravity will effect the particle.
	 */
	public ParticleSystem(List<ParticleTemplate> types, IParticleSpawn spawn, float pps, float speed, float gravityEffect) {
		this.types = types;
		this.spawn = spawn;
		this.pps = pps;
		this.averageSpeed = speed;
		this.gravityEffect = gravityEffect;
		this.randomRotation = false;
		this.random = new Random();

		this.systemCentre = new Vector3f();
		this.centreVelocity = new Vector3f();

		this.paused = false;

		FlounderEngine.getParticles().addSystem(this);
	}

	public List<ParticleTemplate> getTypes() {
		return types;
	}

	public void addParticleType(ParticleTemplate particleTemplate) {
		types.add(particleTemplate);
	}

	public void removeParticleType(ParticleTemplate particleTemplate) {
		types.remove(particleTemplate);
	}

	public IParticleSpawn getSpawn() {
		return spawn;
	}

	public void setSpawn(IParticleSpawn spawn) {
		this.spawn = spawn;
	}

	public float getPps() {
		return pps;
	}

	public void setPps(float pps) {
		this.pps = pps;
	}

	public float getAverageSpeed() {
		return averageSpeed;
	}

	public void setAverageSpeed(float averageSpeed) {
		this.averageSpeed = averageSpeed;
	}

	public float getGravityEffect() {
		return gravityEffect;
	}

	public void setGravityEffect(float gravityEffect) {
		this.gravityEffect = gravityEffect;
	}

	public void randomizeRotation() {
		this.randomRotation = true;
	}

	public Vector3f getSystemCentre() {
		return systemCentre;
	}

	public void setSystemCentre(Vector3f systemCentre) {
		this.systemCentre = systemCentre;
	}

	public Vector3f getCentreVelocity() {
		return centreVelocity;
	}

	public void setCentreVelocity(Vector3f centreVelocity) {
		this.centreVelocity = centreVelocity;
	}

	public void setDirection(Vector3f direction, float deviation) {
		this.direction = new Vector3f(direction);
		this.directionDeviation = ((float) (deviation * Math.PI));
	}

	public void setSpeedError(float error) {
		this.speedError = error;
	}

	public void setLifeError(float error) {
		this.lifeError = error;
	}

	public void setScaleError(float error) {
		this.scaleError = error;
	}

	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	public void generateParticles() {
		if (paused || spawn == null) {
			return;
		}

		float delta = FlounderEngine.getDelta();
		float particlesToCreate = this.pps * delta;
		int count = (int) Math.floor(particlesToCreate);
		float partialParticle = particlesToCreate % 1.0f;

		for (int i = 0; i < count; i++) {
			emitParticle();
		}

		if (Math.random() < partialParticle) {
			emitParticle();
		}
	}

	private void emitParticle() {
		Vector3f velocity;

		if (this.direction != null) {
			velocity = generateRandomUnitVectorWithinCone(direction, directionDeviation);
		} else {
			velocity = generateRandomUnitVector();
		}

		ParticleTemplate emitType = types.get((int) Math.floor(Maths.randomInRange(0, types.size())));

		velocity.normalize();
		velocity.scale(generateValue(averageSpeed, averageSpeed * speedError));
		Vector3f.add(velocity, centreVelocity, velocity);
		float scale = generateValue(emitType.getScale(), emitType.getScale() * scaleError);
		float lifeLength = generateValue(emitType.getLifeLength(), emitType.getLifeLength() * lifeError);
		Vector3f spawnPos = Vector3f.add(systemCentre, spawn.getBaseSpawnPosition(), null);

		new Particle(emitType, new Vector3f(spawnPos), velocity, lifeLength, generateRotation(), scale, gravityEffect);
	}

	private float generateValue(float average, float errorMargin) {
		float offset = (random.nextFloat() - 0.5f) * 2.0f * errorMargin;
		return average + offset;
	}

	private float generateRotation() {
		if (this.randomRotation) {
			return this.random.nextFloat() * 360.0f;
		}

		return 0.0f;
	}

	private static Vector3f generateRandomUnitVectorWithinCone(Vector3f coneDirection, float angle) {
		float cosAngle = (float) Math.cos(angle);
		Random random = new Random();
		float theta = (float) (random.nextFloat() * 2.0f * 3.141592653589793);
		float z = cosAngle + random.nextFloat() * (1.0f - cosAngle);
		float rootOneMinusZSquared = (float) Math.sqrt(1.0f - z * z);
		float x = (float) (rootOneMinusZSquared * Math.cos(theta));
		float y = (float) (rootOneMinusZSquared * Math.sin(theta));

		Vector4f direction = new Vector4f(x, y, z, 1.0f);

		if ((coneDirection.x != 0.0f) || (coneDirection.y != 0.0f) || ((coneDirection.z != 1.0f) && (coneDirection.z != -1.0f))) {
			Vector3f rotateAxis = Vector3f.cross(coneDirection, new Vector3f(0.0f, 0.0f, 1.0f), null);
			rotateAxis.normalize();
			float rotateAngle = (float) Math.acos(Vector3f.dot(coneDirection, new Vector3f(0.0f, 0.0f, 1.0f)));
			Matrix4f rotationMatrix = new Matrix4f();
			Matrix4f.rotate(rotationMatrix, rotateAxis, -rotateAngle, rotationMatrix);
			Matrix4f.transform(rotationMatrix, direction, direction);
		} else if (coneDirection.z == -1.0f) {
			direction.z *= -1.0f;
		}

		return new Vector3f(direction);
	}

	private Vector3f generateRandomUnitVector() {
		float theta = (float) (random.nextFloat() * 2.0f * 3.141592653589793);
		float z = random.nextFloat() * 2.0f - 1.0f;
		float rootOneMinusZSquared = (float) Math.sqrt(1.0f - z * z);
		float x = (float) (rootOneMinusZSquared * Math.cos(theta));
		float y = (float) (rootOneMinusZSquared * Math.sin(theta));
		return new Vector3f(x, y, z);
	}

	public void delete() {
		FlounderEngine.getParticles().removeSystem(this);
	}
}
