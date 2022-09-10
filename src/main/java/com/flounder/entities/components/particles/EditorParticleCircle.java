package com.flounder.entities.components.particles;

import com.flounder.maths.vectors.*;
import com.flounder.particles.spawns.*;

import javax.swing.*;

public class EditorParticleCircle extends IEditorParticleSpawn {
	private SpawnCircle spawn;

	public EditorParticleCircle() {
		spawn = new SpawnCircle(1.0f, new Vector3f(0.0f, 1.0f, 0.0f));
	}

	@Override
	public String getTabName() {
		return "Circle";
	}

	@Override
	public SpawnCircle getComponent() {
		return spawn;
	}

	@Override
	public void addToPanel(JPanel panel) {
		// Radius Slider.
		JSlider radiusSlider = new JSlider(JSlider.HORIZONTAL, 0, 50, (int) spawn.getRadius());
		radiusSlider.setToolTipText("Spawn Radius");
		radiusSlider.addChangeListener(e -> {
			JSlider source = (JSlider) e.getSource();
			int reading = source.getValue();

			if (reading > 1) {
				spawn.setRadius(reading);
			}
		});
		// Turn on labels at major tick marks.
		radiusSlider.setMajorTickSpacing(10);
		radiusSlider.setMinorTickSpacing(2);
		radiusSlider.setPaintTicks(true);
		radiusSlider.setPaintLabels(true);
		panel.add(radiusSlider);

		// TODO: Add heading changer.
	}

	@Override
	public String[] getSavableValues() {
		String saveRadius = spawn.getRadius() + "f";
		String saveHeading = "new Vector3f(" + spawn.getHeading().x + "f, " + spawn.getHeading().y + "f, " + spawn.getHeading().z + "f)";
		return new String[]{saveRadius, saveHeading};
	}
}
