package flounder.particles;

import flounder.engine.*;
import flounder.helpers.*;
import flounder.physics.*;
import flounder.resources.*;

import java.util.*;

import static org.lwjgl.glfw.GLFW.*;

/**
 * A manager that manages particles.
 */
public class FlounderParticles implements IModule {
	public static final MyFile PARTICLES_LOC = new MyFile(MyFile.RES_FOLDER, "particles");

	private List<ParticleSystem> particleSystems;

	private List<List<Particle>> particles;
	private AABB reusableAABB;

	@Override
	public void init() {
		particleSystems = new ArrayList<>();
		particles = new ArrayList<>();
		reusableAABB = new AABB();
	}

	@Override
	public void update() {
		if (FlounderEngine.isGamePaused()) {
			return;
		}

		if (!FlounderEngine.getDevices().getKeyboard().getKey(GLFW_KEY_Y)) {
			particleSystems.forEach(ParticleSystem::generateParticles);
		}

		int totalParticles = 0;
		int visibleParticles = 0;

		try {
			for (List<Particle> list : particles) {
				Iterator<Particle> iterator = list.iterator();

				while (iterator.hasNext()) {
					// Iterate and update the particles.
					Particle particle = iterator.next();
					particle.update(!FlounderEngine.getDevices().getKeyboard().getKey(GLFW_KEY_Y));

					// Update particle visibility.
					float SIZE = 0.5f * particle.getParticleTemplate().getScale();
					reusableAABB.getMinExtents().set(particle.getPosition().getX() - SIZE, particle.getPosition().getY() - SIZE, particle.getPosition().getZ() - SIZE);
					reusableAABB.getMaxExtents().set(particle.getPosition().getX() + SIZE, particle.getPosition().getY() + SIZE, particle.getPosition().getZ() + SIZE);
					particle.setVisable(FlounderEngine.getCamera().getViewFrustum().aabbInFrustum(reusableAABB));
					visibleParticles += particle.isVisable() ? 1 : 0;

					// Remove particles that are not alive.
					if (!particle.isAlive()) {
						iterator.remove();

						if (list.isEmpty()) {
							particles.remove(list);
						}
					} else {
						totalParticles++;
					}
				}
			}
		} catch (ConcurrentModificationException e) {
			FlounderEngine.getLogger().exception(e);
		}

		for (List<Particle> list : particles) {
			// Added to engine.particles first -> last, so no initial reverse needed.
			ArraySorting.heapSort(list); // insertionSort
			Collections.reverse(list); // Reverse as the sorted list should be close(small) -> far(big).
		}

		if (FlounderEngine.getProfiler().isOpen()) {
			FlounderEngine.getProfiler().add("Particles", "Systems", particleSystems.size());
			FlounderEngine.getProfiler().add("Particles", "Types", particles.size());
			FlounderEngine.getProfiler().add("Particles", "Particles", totalParticles);
			FlounderEngine.getProfiler().add("Particles", "Visible", visibleParticles);
		}
	}

	@Override
	public void profile() {

	}

	@Override
	public void dispose() {

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
	protected List<List<Particle>> getParticles() {
		return particles;
	}

	/**
	 * Adds a particle to the update loop.
	 *
	 * @param particle The particle to add.
	 */
	protected void addParticle(final Particle particle) {
		for (List<Particle> list : particles) {
			if (list.get(0).getParticleTemplate().equals(particle.getParticleTemplate())) {
				list.add(particle);
				return;
			}
		}

		List<Particle> list = new ArrayList<>();
		list.add(particle);
		particles.add(list);
	}
}
