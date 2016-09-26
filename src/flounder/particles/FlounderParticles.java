package flounder.particles;

import flounder.devices.*;
import flounder.engine.*;
import flounder.helpers.*;
import flounder.loaders.*;
import flounder.logger.*;
import flounder.maths.vectors.*;
import flounder.particles.loading.*;
import flounder.profiling.*;
import flounder.resources.*;
import flounder.space.*;
import flounder.textures.*;

import java.util.*;

/**
 * A manager that manages particles.
 */
public class FlounderParticles extends IModule {
	private static final FlounderParticles instance = new FlounderParticles();

	public static final MyFile PARTICLES_LOC = new MyFile(MyFile.RES_FOLDER, "particles");
	public static final float MAX_ELAPED_TIME = 5.0f;

	private List<ParticleSystem> particleSystems;
	private List<StructureBasic<Particle>> particles;
	private List<Particle> deadParticles;

	public FlounderParticles() {
		super(FlounderLogger.class, FlounderProfiler.class, FlounderDisplay.class, FlounderLoader.class, FlounderTextures.class);
	}

	@Override
	public void init() {
		this.particleSystems = new ArrayList<>();
		this.particles = new ArrayList<>();
		this.deadParticles = new ArrayList<>();
	}

	@Override
	public void update() {
		if (FlounderEngine.isGamePaused()) {
			return;
		}

		particleSystems.forEach(ParticleSystem::generateParticles);

		for (StructureBasic<Particle> list : particles) {
			List<Particle> particles = list.getAll(new ArrayList<>());

			for (Particle particle : particles) {
				particle.update();

				if (!particle.isAlive()) {
					list.remove(particle);
					deadParticles.add(particle);
				}
			}
		}

		Iterator<Particle> deadIterator = deadParticles.iterator();

		while (deadIterator.hasNext()) {
			Particle particle = deadIterator.next();
			particle.update();

			if (particle.getElapsedTime() > MAX_ELAPED_TIME) {
				deadIterator.remove();
			}
		}

		ArraySorting.heapSort(deadParticles); // Sorts the list old to new.
	}

	@Override
	public void profile() {
		FlounderProfiler.add("Particles", "Systems", particleSystems.size());
		FlounderProfiler.add("Particles", "Types", particles.size());
		FlounderProfiler.add("Particles", "Dead Particles", deadParticles.size());
	}

	/**
	 * Clears all particles from the scene.
	 */
	protected static void cleat() {
		instance.particles.clear();
	}

	/**
	 * Adds a particle system to the update loop.
	 *
	 * @param system The new system to add.
	 */
	public static void addSystem(ParticleSystem system) {
		instance.particleSystems.add(system);
	}

	/**
	 * Removes a particle system from the update loop.
	 *
	 * @param system The system to remove.
	 */
	public static void removeSystem(ParticleSystem system) {
		instance.particleSystems.remove(system);
	}

	/**
	 * Gets a list of all particles.
	 *
	 * @return All particles.
	 */
	protected static List<StructureBasic<Particle>> getParticles() {
		return instance.particles;
	}

	/**
	 * Adds a particle to the update loop.
	 *
	 * @param particleTemplate The particle template to build from.
	 * @param position The particles initial position.
	 * @param velocity The particles initial velocity.
	 * @param lifeLength The particles life length.
	 * @param rotation The particles rotation.
	 * @param scale The particles scale.
	 * @param gravityEffect The particles gravity effect.
	 */
	public static void addParticle(ParticleTemplate particleTemplate, Vector3f position, Vector3f velocity, float lifeLength, float rotation, float scale, float gravityEffect) {
		Particle particle;

		if (instance.deadParticles.size() > 0) {
			particle = instance.deadParticles.get(0).set(particleTemplate, position, velocity, lifeLength, rotation, scale, gravityEffect);
			instance.deadParticles.remove(0);
		} else {
			particle = new Particle(particleTemplate, position, velocity, lifeLength, rotation, scale, gravityEffect);
		}

		for (StructureBasic<Particle> list : instance.particles) {
			if (list.getSize() > 0 && list.get(0).getParticleTemplate().equals(particle.getParticleTemplate())) {
				list.add(particle);
				return;
			}
		}

		StructureBasic<Particle> list = new StructureBasic<>();
		list.add(particle);
		instance.particles.add(list);
	}

	@Override
	public IModule getInstance() {
		return instance;
	}

	@Override
	public void dispose() {
	}
}
