package flounder.entities.components.particles;

import flounder.maths.vectors.*;
import flounder.particles.spawns.*;

import javax.swing.*;
import javax.swing.event.*;

public class EditorParticleLine extends IEditorParticleSpawn {
	private SpawnLine spawn;

	public EditorParticleLine() {
		spawn = new SpawnLine(1.0f, new Vector3f(1.0f, 0.0f, 0.0f));
	}

	@Override
	public String getTabName() {
		return "Line";
	}

	@Override
	public SpawnLine getComponent() {
		return spawn;
	}

	@Override
	public void addToPanel(JPanel panel) {
		// Length Slider.
		JSlider lengthSlider = new JSlider(JSlider.HORIZONTAL, 0, 50, (int) spawn.getLength());
		lengthSlider.setToolTipText("Spawn Length");
		lengthSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				int reading = source.getValue();

				if (reading >= 1) {
					spawn.setLength(reading);
				}
			}
		});
		// Turn on labels at major tick marks.
		lengthSlider.setMajorTickSpacing(10);
		lengthSlider.setMinorTickSpacing(2);
		lengthSlider.setPaintTicks(true);
		lengthSlider.setPaintLabels(true);
		panel.add(lengthSlider);
	}

	@Override
	public String[] getSavableValues() {
		String saveLength = spawn.getLength() + "f";
		String saveAxis = "new Vector3f(" + spawn.getAxis().x + "f, " + spawn.getAxis().y + "f, " + spawn.getAxis().z + "f)";
		return new String[]{saveLength, saveAxis};
	}
}
