package flounder.entities.components;

import flounder.entities.*;
import flounder.helpers.*;
import flounder.logger.*;
import flounder.maths.vectors.*;
import flounder.particles.*;
import flounder.particles.spawns.*;
import flounder.resources.*;
import flounder.textures.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.lang.reflect.*;
import java.util.*;

public class ComponentParticles extends IComponentEntity implements IComponentEditor {
	private kosmos.entities.components.particles.IEditorParticleSpawn[] spawns = new kosmos.entities.components.particles.IEditorParticleSpawn[]{
			new kosmos.entities.components.particles.EditorParticleCircle(),
			new kosmos.entities.components.particles.EditorParticleLine(),
			new kosmos.entities.components.particles.EditorParticlePoint(),
			new kosmos.entities.components.particles.EditorParticleSphere(),
	};

	private ParticleSystem particleSystem;
	private Vector3f centreOffset;
	private Vector3f lastPosition;

	public kosmos.entities.components.particles.IEditorParticleSpawn editorSystemSpawn;

	/**
	 * Creates a new ComponentParticles.
	 *
	 * @param entity The entity this component is attached to.
	 */
	public ComponentParticles(Entity entity) {
		this(entity, new ArrayList<>(), null, new Vector3f(), 100.0f, 1.0f, 1.0f);
	}

	/**
	 * Creates a new ComponentParticles.
	 *
	 * @param entity The entity this component is attached to.
	 * @param types
	 * @param spawn
	 * @param pps
	 * @param speed
	 * @param gravityEffect
	 */
	public ComponentParticles(Entity entity, List<ParticleType> types, IParticleSpawn spawn, Vector3f offset, float pps, float speed, float gravityEffect) {
		super(entity);

		this.particleSystem = new ParticleSystem(types, spawn, pps, speed, gravityEffect);
		this.particleSystem.setSystemCentre(new Vector3f());
		this.centreOffset = offset;
		this.lastPosition = new Vector3f();

		if (entity != null) {
			this.lastPosition.set(entity.getPosition());
		}
	}

	@Override
	public void update() {
		if (particleSystem != null) {
			if (particleSystem.getTypes().isEmpty()) {
				particleSystem.addParticleType(new ParticleType("rain", TextureFactory.newBuilder().setFile(new MyFile(FlounderParticles.PARTICLES_FOLDER, "rainParticle.png")).setNumberOfRows(4).create(), 3.5f, 0.15f));
				particleSystem.addParticleType(new ParticleType("snow", TextureFactory.newBuilder().setFile(new MyFile(FlounderParticles.PARTICLES_FOLDER, "snowParticle.png")).setNumberOfRows(4).create(), 3.5f, 0.20f));
			}

			if (super.getEntity().hasMoved()) {
				Vector3f translated = new Vector3f(centreOffset);
				Vector3f.rotate(translated, super.getEntity().getRotation(), translated);
				Vector3f.add(translated, super.getEntity().getPosition(), translated);

				Vector3f difference = Vector3f.subtract(lastPosition, translated, null);
				lastPosition.set(translated);

				particleSystem.setSystemCentre(translated);
				particleSystem.setVelocityCentre(difference);
			}
		}
	}

	public ParticleSystem getParticleSystem() {
		return particleSystem;
	}

	public Vector3f getCentreOffset() {
		return centreOffset;
	}

	@Override
	public void addToPanel(JPanel panel) {
		// PPS Slider.
		JSlider ppsSlider = new JSlider(JSlider.HORIZONTAL, 0, 2500, (int) particleSystem.getPPS());
		ppsSlider.setToolTipText("Particles Per Second");
		ppsSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				int reading = source.getValue();
				particleSystem.setPps(reading);
			}
		});
		ppsSlider.setMajorTickSpacing(500);
		ppsSlider.setMinorTickSpacing(100);
		ppsSlider.setPaintTicks(true);
		ppsSlider.setPaintLabels(true);
		panel.add(ppsSlider);

		// Gravity Effect Slider.
		JSlider gravityEffectSlider = new JSlider(JSlider.HORIZONTAL, -150, 150, (int) (particleSystem.getGravityEffect() * 100.0f));
		gravityEffectSlider.setToolTipText("Gravity Effect");
		gravityEffectSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				int reading = source.getValue();
				particleSystem.setGravityEffect(reading / 100.0f);
			}
		});
		gravityEffectSlider.setMajorTickSpacing(50);
		gravityEffectSlider.setMinorTickSpacing(10);
		gravityEffectSlider.setPaintTicks(true);
		gravityEffectSlider.setPaintLabels(true);
		panel.add(gravityEffectSlider);

		// Speed Slider.
		JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, 0, 150, (int) (particleSystem.getAverageSpeed() * 10.0f));
		speedSlider.setToolTipText("Speed Slider");
		speedSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				int reading = source.getValue();
				particleSystem.setAverageSpeed(reading / 10.0f);
			}
		});
		speedSlider.setMajorTickSpacing(30);
		speedSlider.setMinorTickSpacing(5);
		speedSlider.setPaintTicks(true);
		speedSlider.setPaintLabels(true);
		panel.add(speedSlider);

		// X Offset Field.
		JSpinner xOffsetField = new JSpinner(new SpinnerNumberModel((double) centreOffset.x, Double.NEGATIVE_INFINITY + 1.0, Double.POSITIVE_INFINITY - 1.0, 0.1));
		xOffsetField.setToolTipText("Particle System X Offset");
		xOffsetField.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				centreOffset.x = (float) (double) ((JSpinner) e.getSource()).getValue();
			}
		});
		panel.add(xOffsetField);

		// Y Offset Field.
		JSpinner yOffsetField = new JSpinner(new SpinnerNumberModel((double) centreOffset.x, Double.NEGATIVE_INFINITY + 1.0, Double.POSITIVE_INFINITY - 1.0, 0.1));
		yOffsetField.setToolTipText("Particle System Y Offset");
		yOffsetField.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				centreOffset.y = (float) (double) ((JSpinner) e.getSource()).getValue();
			}
		});
		panel.add(yOffsetField);

		// Z Offset Field.
		JSpinner zOffsetField = new JSpinner(new SpinnerNumberModel((double) centreOffset.x, Double.NEGATIVE_INFINITY + 1.0, Double.POSITIVE_INFINITY - 1.0, 0.1));
		yOffsetField.setToolTipText("Particle System Z Offset");
		zOffsetField.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				centreOffset.z = (float) (double) ((JSpinner) e.getSource()).getValue();
			}
		});
		panel.add(zOffsetField);

		// Component Dropdown.
		JComboBox componentDropdown = new JComboBox();
		for (int i = 0; i < spawns.length; i++) {
			componentDropdown.addItem(spawns[i].getTabName());
		}
		panel.add(componentDropdown);

		// Component Add Button.
		JButton componentAdd = new JButton("Set Spawn");
		componentAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String spawn = (String) componentDropdown.getSelectedItem();
				kosmos.entities.components.particles.IEditorParticleSpawn particleSpawn = null;

				for (int i = 0; i < spawns.length; i++) {
					if (spawns[i].getTabName().equals(spawn)) {
						try {
							FlounderLogger.log("Adding component: " + spawn);
							Class componentClass = Class.forName(spawns[i].getClass().getName());
							Class[] componentTypes = new Class[]{};
							@SuppressWarnings("unchecked") Constructor componentConstructor = componentClass.getConstructor(componentTypes);
							Object[] componentParameters = new Object[]{};
							particleSpawn = (kosmos.entities.components.particles.IEditorParticleSpawn) componentConstructor.newInstance(componentParameters);
						} catch (ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException ex) {
							FlounderLogger.error("While loading particle spawn" + spawns[i] + "'s constructor could not be found!");
							FlounderLogger.exception(ex);
						}
					}
				}

				if (particleSystem.getSpawn() != null) {
					String classname = particleSystem.getSpawn().getClass().getName();
					ComponentsList.REMOVE_SIDE_TAB.add("Particles (" + classname.split("\\.")[ByteWork.getCharCount(classname, '.')].replace("Spawn", "") + ")");
				}

				if (particleSpawn != null) {
					particleSystem.setSpawn(particleSpawn.getComponent());
					editorSystemSpawn = particleSpawn;

					JPanel panel = IComponentEditor.makeTextPanel();
					particleSpawn.addToPanel(panel);
					ComponentsList.ADD_SIDE_TAB.add(new Pair<>("Particles (" + particleSpawn.getTabName() + ")", panel));
				}
			}
		});
		panel.add(componentAdd);

		// TODO: Add selection list for particle templates to be used in the types list.
	}

	@Override
	public void editorUpdate() {
	}

	@Override
	public Pair<String[], String[]> getSaveValues(String entityName) {
		String parameterData = "";

		for (String s : editorSystemSpawn.getSavableValues()) {
			parameterData += s + ", ";
		}

		parameterData = parameterData.replaceAll(", $", "");

		String particlesData = "";

		for (ParticleType t : particleSystem.getTypes()) {
			String saveTexture = (t.getTexture() != null) ? ("TextureFactory.newBuilder().setFile(new MyFile(FlounderParticles.PARTICLES_FOLDER, \"" + t.getName() + "Particle.png\")).setNumberOfRows(" + t.getTexture().getNumberOfRows() + ").create()") : null;
			particlesData += "new ParticleType(\"" + t.getName() + "\", " + saveTexture + ", " + t.getLifeLength() + "f, " + t.getScale() + "f), ";
		}

		particlesData = particlesData.replaceAll(", $", "");

		String saveParticles = "new ParticleType[]{" + particlesData + "}";
		String saveSpawn = "new " + particleSystem.getSpawn().getClass().getName() + "(" + parameterData + ")";
		String saveParticleOffset = "new Vector3f(" + centreOffset.x + "f, " + centreOffset.y + "f, " + centreOffset.z + "f)";
		String saveParticlePPS = particleSystem.getPPS() + "f";
		String saveParticleSpeed = particleSystem.getAverageSpeed() + "f";
		String saveParticleGravity = particleSystem.getGravityEffect() + "f";

		return new Pair<>(
				new String[]{"private static final ParticleType[] TEMPLATES = " + saveParticles}, // Static variables
				new String[]{"Arrays.asList(TEMPLATES)", saveSpawn, saveParticleOffset, saveParticlePPS, saveParticleSpeed, saveParticleGravity} // Class constructor
		);
	}

	@Override
	public void dispose() {
		FlounderParticles.removeSystem(particleSystem);
		particleSystem = null;
	}
}
