package flounder.entities.components;

import flounder.entities.*;
import flounder.helpers.*;
import flounder.shaders.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;

/**
 * Creates a set of lighting data for a entity.
 */
public class ComponentSurface extends IComponentEntity implements IComponentRender, IComponentEditor {
	private float shineDamper;
	private float reflectivity;

	private boolean ignoreLighting;
	private boolean ignoreFog;
	private boolean projectsShadow;

	/**
	 * Creates a new ComponentSurface.
	 *
	 * @param entity The entity this component is attached to.
	 */
	public ComponentSurface(Entity entity) {
		this(entity, 1.0f, 0.0f, false, false, true);
	}

	/**
	 * Creates a new ComponentSurface.
	 *
	 * @param entity The entity this component is attached to.
	 * @param shineDamper The rendered objects shine damper when lighted.
	 * @param reflectivity The rendered objects reflectivity when lighted.
	 * @param ignoreLighting If the rendered object will ignore shadows and lights.
	 * @param ignoreFog If the rendered object will ignore fog.
	 * @param projectsShadow If a shadow will be projected from the entities model.
	 */
	public ComponentSurface(Entity entity, float shineDamper, float reflectivity, boolean ignoreLighting, boolean ignoreFog, boolean projectsShadow) {
		super(entity);

		this.shineDamper = shineDamper;
		this.reflectivity = reflectivity;

		this.ignoreLighting = ignoreLighting;
		this.ignoreFog = ignoreFog;
		this.projectsShadow = projectsShadow;
	}

	@Override
	public void update() {
	}

	public float getShineDamper() {
		return shineDamper;
	}

	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}

	public boolean isIgnoreLighting() {
		return ignoreLighting;
	}

	public void setIgnoreLighting(boolean ignoreLighting) {
		this.ignoreLighting = ignoreLighting;
	}

	public boolean isIgnoreFog() {
		return ignoreFog;
	}

	public void setIgnoreFog(boolean ignoreFog) {
		this.ignoreFog = ignoreFog;
	}

	public boolean isProjectsShadow() {
		return projectsShadow;
	}

	public void setProjectsShadow(boolean projectsShadow) {
		this.projectsShadow = projectsShadow;
	}

	@Override
	public void render(ShaderObject shader, Single<Integer> vaoLength) {
		shader.getUniformFloat("shineDamper").loadFloat(shineDamper);
		shader.getUniformFloat("reflectivity").loadFloat(reflectivity);
		shader.getUniformBool("ignoreFog").loadBoolean(ignoreFog);
		shader.getUniformBool("ignoreLighting").loadBoolean(ignoreLighting);
	}

	@Override
	public void renderClear(ShaderObject shader) {
		shader.getUniformFloat("shineDamper").loadFloat(1.0f);
		shader.getUniformFloat("reflectivity").loadFloat(0.0f);
		shader.getUniformBool("ignoreFog").loadBoolean(false);
		shader.getUniformBool("ignoreLighting").loadBoolean(false);
	}

	@Override
	public void addToPanel(JPanel panel) {
		// Shine Damper Slider.
		JSlider sliderShineDamper = new JSlider(JSlider.HORIZONTAL, 0, 500, (int) (shineDamper * 100.0f));
		sliderShineDamper.setToolTipText("Shine Damper");
		sliderShineDamper.addChangeListener((ChangeEvent e) -> {
			JSlider source = (JSlider) e.getSource();
			int reading = source.getValue();
			this.shineDamper = (float) reading / 100.0f;
		});
		sliderShineDamper.setMajorTickSpacing(100);
		sliderShineDamper.setMinorTickSpacing(50);
		sliderShineDamper.setPaintTicks(true);
		sliderShineDamper.setPaintLabels(true);
		panel.add(sliderShineDamper);

		// Reflectivity Slider.
		JSlider sliderReflectivity = new JSlider(JSlider.HORIZONTAL, 0, 500, (int) (reflectivity * 100.0f));
		sliderReflectivity.setToolTipText("Reflectivity");
		sliderReflectivity.addChangeListener((ChangeEvent e) -> {
			JSlider source = (JSlider) e.getSource();
			int reading = source.getValue();
			this.reflectivity = (float) reading / 100.0f;
		});
		sliderReflectivity.setMajorTickSpacing(100);
		sliderReflectivity.setMinorTickSpacing(50);
		sliderReflectivity.setPaintTicks(true);
		sliderReflectivity.setPaintLabels(true);
		panel.add(sliderReflectivity);

		// Ignore Fog Checkbox.
		JCheckBox boxIgnoreFog = new JCheckBox("Ignore Fog");
		boxIgnoreFog.setSelected(this.ignoreFog);
		boxIgnoreFog.addItemListener((ItemEvent e) -> this.ignoreFog = boxIgnoreFog.isSelected());
		panel.add(boxIgnoreFog);

		// Ignore Shadows Checkbox.
		JCheckBox boxIgnoreShadows = new JCheckBox("Ignore Shadows");
		boxIgnoreShadows.setSelected(this.ignoreLighting);
		boxIgnoreShadows.addItemListener((ItemEvent e) -> this.ignoreLighting = boxIgnoreShadows.isSelected());
		panel.add(boxIgnoreShadows);

		// Project Shadows Checkbox.
		JCheckBox boxProjectShadows = new JCheckBox("Project Shadows");
		boxProjectShadows.setSelected(this.ignoreLighting);
		boxProjectShadows.addItemListener((ItemEvent e) -> this.projectsShadow = boxProjectShadows.isSelected());
		panel.add(boxProjectShadows);
	}

	@Override
	public void editorUpdate() {

	}

	@Override
	public Pair<String[], String[]> getSaveValues(String entityName) {
		String saveShineDamper = shineDamper + "f";
		String saveReflectivity = reflectivity + "f";
		String saveIgnoreFog = ignoreFog + "";
		String saveIgnoreShadows = ignoreLighting + "";
		String saveProjectShadows = projectsShadow + "";

		return new Pair<>(
				new String[]{}, // Static variables
				new String[]{saveShineDamper, saveReflectivity, saveIgnoreFog, saveIgnoreShadows, saveProjectShadows} // Class constructor
		);
	}

	@Override
	public void dispose() {
	}
}
