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

import java.io.*;
import java.lang.ref.*;
import java.util.*;

/**
 * A manager that manages particles.
 */
public class FlounderParticles extends IModule {
	private static final FlounderParticles instance = new FlounderParticles();

	public static final MyFile PARTICLES_LOC = new MyFile(MyFile.RES_FOLDER, "particles");
	public static final float MAX_ELAPED_TIME = 5.0f;

	private Map<String, SoftReference<ParticleTemplate>> loaded;

	private List<ParticleSystem> particleSystems;
	private List<StructureBasic<Particle>> particles;
	private List<Particle> deadParticles;

	/**
	 * Creates a new particle systems manager.
	 */
	public FlounderParticles() {
		super(ModuleUpdate.AFTER_ENTRANCE, FlounderLogger.class, FlounderProfiler.class, FlounderDisplay.class, FlounderLoader.class, FlounderTextures.class);
	}

	@Override
	public void init() {
		this.loaded = new HashMap<>();

		this.particleSystems = new ArrayList<>();
		this.particles = new ArrayList<>();
		this.deadParticles = new ArrayList<>();
	}

	@Override
	public void run() {
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
	public static void clear() {
		instance.particles.clear();
	}

	/**
	 * Adds a particle system to the run loop.
	 *
	 * @param system The new system to add.
	 */
	public static void addSystem(ParticleSystem system) {
		instance.particleSystems.add(system);
	}

	/**
	 * Removes a particle system from the run loop.
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
	 * Loads a particle type into a template.
	 *
	 * @param name The particle type name.
	 *
	 * @return The loaded template.
	 */
	public static ParticleTemplate load(String name) {
		SoftReference<ParticleTemplate> ref = instance.loaded.get(name);
		ParticleTemplate data = ref == null ? null : ref.get();

		if (data == null) {
			FlounderLogger.log(name + " is being loaded into a particle type right now!");
			instance.loaded.remove(name);

			// Creates the file reader.
			MyFile saveFile = new MyFile(FlounderParticles.PARTICLES_LOC, name + ".particle");

			try {
				BufferedReader fileReader = saveFile.getReader();

				if (fileReader == null) {
					FlounderLogger.error("Error creating reader the particle file: " + saveFile);
					return null;
				}

				// Loaded data.
				String particleName = "unnamed";
				String textureFile = "/";
				String numberOfRows = "1";
				String lifeLength = "1.0f";
				String scale = "1.0f";

				// Current line.
				String line;

				// Each line read loop.
				while ((line = fileReader.readLine()) != null) {
					// Entity General Data.
					if (line.contains("ParticleData")) {
						while (!(line = fileReader.readLine()).contains("};")) {
							if (line.contains("Name")) {
								particleName = line.replaceAll("\\s+", "").replaceAll(";", "").substring("Name:".length());
							} else if (line.contains("Texture")) {
								textureFile = line.replaceAll("\\s+", "").replaceAll(";", "").substring("Texture:".length());
							} else if (line.contains("NumberOfRows")) {
								numberOfRows = line.replaceAll("\\s+", "").replaceAll(";", "").substring("NumberOfRows:".length());
							} else if (line.contains("LifeLength")) {
								lifeLength = line.replaceAll("\\s+", "").replaceAll(";", "").substring("LifeLength:".length());
							} else if (line.contains("Scale")) {
								scale = line.replaceAll("\\s+", "").replaceAll(";", "").substring("Scale:".length());
							}
						}
					}
				}

				Texture texture = Texture.newTexture(new MyFile(textureFile)).create();
				texture.setNumberOfRows(Integer.parseInt(numberOfRows));
				data = new ParticleTemplate(particleName, texture, Float.parseFloat(lifeLength), Float.parseFloat(scale));
			} catch (IOException e) {
				FlounderLogger.error("File reader for particle " + saveFile.getPath() + " did not execute successfully!");
				FlounderLogger.exception(e);
				return null;
			}

			instance.loaded.put(name, new SoftReference<>(data));
		}

		return data;
	}

	/**
	 * Adds a particle to the run loop.
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
