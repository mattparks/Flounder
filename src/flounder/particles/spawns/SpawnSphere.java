package flounder.particles.spawns;

import flounder.maths.*;
import flounder.maths.vectors.*;

import javax.swing.*;
import javax.swing.event.*;

public class SpawnSphere implements IParticleSpawn {
	private float radius;
	private Vector3f spawnPosition;

	public SpawnSphere() {
		this.radius = 1.0f;
		this.spawnPosition = new Vector3f();
	}

	public SpawnSphere(float radius) {
		this.radius = radius;
		this.spawnPosition = new Vector3f();
	}

	public SpawnSphere(String[] template) {
		this.radius = Float.parseFloat(template[0]);
		this.spawnPosition = new Vector3f();
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	@Override
	public String[] getSavableValues() {
		return new String[]{"" + radius};
	}

	@Override
	public Vector3f getBaseSpawnPosition() {
		Maths.generateRandomUnitVector(spawnPosition);

		spawnPosition.scale(radius);
		float a = Maths.RANDOM.nextFloat();
		float b = Maths.RANDOM.nextFloat();

		if (a > b) {
			float temp = a;
			a = b;
			b = temp;
		}

		float randX = (float) (b * Math.cos(6.283185307179586 * (a / b)));
		float randY = (float) (b * Math.sin(6.283185307179586 * (a / b)));
		float distance = new Vector2f(randX, randY).length();
		spawnPosition.scale(distance);
		return spawnPosition;
	}

	@Override
	public void addToPanel(JPanel panel) {
		// Radius Slider.
		JSlider radiusSlider = new JSlider(JSlider.HORIZONTAL, 0, 50, 1);
		radiusSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				int reading = source.getValue();

				if (reading > 1) {
					SpawnSphere.this.radius = reading;
				}
			}
		});
		//Turn on labels at major tick marks.
		radiusSlider.setMajorTickSpacing(10);
		radiusSlider.setMinorTickSpacing(2);
		radiusSlider.setPaintTicks(true);
		radiusSlider.setPaintLabels(true);
		panel.add(radiusSlider);
	}
}
