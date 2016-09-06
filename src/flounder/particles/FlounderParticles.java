package flounder.particles;

import flounder.engine.*;
import flounder.helpers.*;
import flounder.maths.vectors.*;
import flounder.particles.loading.*;
import flounder.resources.*;
import flounder.space.*;

import java.util.*;

/**
 * A manager that manages particles.
 */
public class FlounderParticles implements IModule {
	public static final MyFile PARTICLES_LOC = new MyFile(MyFile.RES_FOLDER, "particles");
	public static final float MAX_ELAPED_TIME = 5.0f;

	private List<ParticleSystem> particleSystems;
	private List<StructureBasic<Particle>> particles;
	private List<Particle> deadParticles;

	@Override
	public void init() {
		particleSystems = new ArrayList<>();
		particles = new ArrayList<>();
		deadParticles = new ArrayList<>();
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

	public void clearAllParticles() {
		particles.clear();
	}

	@Override
	public void profile() {
		FlounderEngine.getProfiler().add("Particles", "Systems", particleSystems.size());
		FlounderEngine.getProfiler().add("Particles", "Types", particles.size());
		FlounderEngine.getProfiler().add("Particles", "Dead Particles", deadParticles.size());
	}

	/**
	 * Adds a particle system to the update loop.
	 *
	 * @param system The new system to add.
	 */
	public void addSystem(ParticleSystem system) {
		particleSystems.add(system);
	}

	/**
	 * Removes a particle system from the update loop.
	 *
	 * @param system The system to remove.
	 */
	public void removeSystem(ParticleSystem system) {
		particleSystems.remove(system);
	}

	/**
	 * Gets a list of all particles.
	 *
	 * @return All particles.
	 */
	protected List<StructureBasic<Particle>> getParticles() {
		return particles;
	}

	/**
	 * Adds a particle to the update loop.
	 *
	 * @param particleTemplate
	 * @param position
	 * @param velocity
	 * @param lifeLength
	 * @param rotation
	 * @param scale
	 * @param gravityEffect
	 */
	protected void addParticle(ParticleTemplate particleTemplate, Vector3f position, Vector3f velocity, float lifeLength, float rotation, float scale, float gravityEffect) {
		Particle particle;

		if (deadParticles.size() > 0) {
			particle = deadParticles.get(0).set(particleTemplate, position, velocity, lifeLength, rotation, scale, gravityEffect);
			deadParticles.remove(0);
		} else {
			particle = new Particle(particleTemplate, position, velocity, lifeLength, rotation, scale, gravityEffect);
		}

		for (StructureBasic<Particle> list : particles) {
			if (list.getSize() > 0 && list.get(0).getParticleTemplate().equals(particle.getParticleTemplate())) {
				list.add(particle);
				return;
			}
		}

		StructureBasic<Particle> list = new StructureBasic<>();
		list.add(particle);
		particles.add(list);
	}

	@Override
	public void dispose() {
	}
}
