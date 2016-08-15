package flounder.particles.spawns;

import flounder.maths.vectors.*;

import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

public class SpawnLine implements IParticleSpawn {
	private float length;
	private Vector3f axis;
	private Random random;
	private Vector3f spawnPosition;

	public SpawnLine() {
		this.length = 1.0f;
		this.axis = new Vector3f(1, 0, 0);
		this.random = new Random();
		this.spawnPosition = new Vector3f();
	}

	public SpawnLine(float length, Vector3f axis) {
		this.length = length;
		this.axis = axis.normalize();
		this.random = new Random();
		this.spawnPosition = new Vector3f();
	}

	public SpawnLine(String[] template) {
		this.length = Float.parseFloat(template[0]);
		this.axis = new Vector3f().set(template[1]).normalize();
		this.random = new Random();
		this.spawnPosition = new Vector3f();
	}

	public float getLength() {
		return length;
	}

	public void setLength(float length) {
		this.length = length;
	}

	public Vector3f getAxis() {
		return axis;
	}

	public void setAxis(Vector3f axis) {
		this.axis = axis;
	}

	@Override
	public String[] getSavableValues() {
		return new String[]{"" + length, axis.toString()};
	}

	@Override
	public Vector3f getBaseSpawnPosition() {
		spawnPosition.set(axis.x * length, axis.y * length, axis.z * length);
		spawnPosition.scale(random.nextFloat() - 0.5f);
		return spawnPosition;
	}

	@Override
	public void addToPanel(JPanel panel) {
		// Length Slider.
		JSlider lengthSlider = new JSlider(JSlider.HORIZONTAL, 0, 50, (int) length);
		lengthSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				int reading = source.getValue();

				if (reading >= 1) {
					SpawnLine.this.length = reading;
				}
			}
		});
		//Turn on labels at major tick marks.
		lengthSlider.setMajorTickSpacing(10);
		lengthSlider.setMinorTickSpacing(2);
		lengthSlider.setPaintTicks(true);
		lengthSlider.setPaintLabels(true);
		panel.add(lengthSlider);
	}
}
