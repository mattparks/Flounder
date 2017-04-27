package flounder.entities.components;

import flounder.entities.*;
import flounder.helpers.*;
import flounder.lights.*;
import flounder.maths.*;
import flounder.maths.vectors.*;

import javax.swing.*;
import javax.swing.event.*;

public class ComponentLight extends IComponentEntity implements IComponentEditor {
	private Vector3f offset;
	private Colour colour;
	private Attenuation attenuation;

	private Light light;

	/**
	 * Creates a new ComponentLight.
	 *
	 * @param entity The entity this component is attached to.
	 */
	public ComponentLight(Entity entity) {
		this(entity, new Vector3f(), new Colour(1.0f, 1.0f, 1.0f), new Attenuation(1.0f, 0.0f, 0.0f));
	}

	/**
	 * Creates a new ComponentLight.
	 *
	 * @param entity The entity this component is attached to.
	 * @param offset
	 * @param colour
	 * @param attenuation
	 */
	public ComponentLight(Entity entity, Vector3f offset, Colour colour, Attenuation attenuation) {
		super(entity);

		this.offset = offset;
		this.colour = colour;
		this.attenuation = attenuation;

		if (entity != null) {
			this.light = new Light(colour, Vector3f.add(entity.getPosition(), offset, null), attenuation);
		} else {
			this.light = new Light(colour, new Vector3f(offset), attenuation);
		}
	}

	@Override
	public void update() {
		//	if (super.getEntity().hasMoved()) { // TODO
		if (offset != null) {
			Vector3f.add(getEntity().getPosition(), offset, light.getPosition());
		}

		if (colour != null) {
			light.colour.set(colour).scale(getEntity().getAlpha());
		}

		if (attenuation != null) {
			light.attenuation.set(attenuation);
		}
		//	}
	}

	public Vector3f getOffset() {
		return offset;
	}

	public Colour getColour() {
		return colour;
	}

	public Attenuation getAttenuation() {
		return attenuation;
	}

	public Light getLight() {
		return light;
	}

	@Override
	public void addToPanel(JPanel panel) {
		// Attenuation Constant Slider.
		JSlider sliderAttenuationC = new JSlider(JSlider.HORIZONTAL, 0, 500, (int) (light.attenuation.constant * 100.0f));
		sliderAttenuationC.setToolTipText("Attenuation Constant");
		sliderAttenuationC.addChangeListener((ChangeEvent e) -> {
			JSlider source = (JSlider) e.getSource();
			int reading = source.getValue();
			light.attenuation.constant = reading / 100.0f;
		});
		sliderAttenuationC.setMajorTickSpacing(100);
		sliderAttenuationC.setMinorTickSpacing(50);
		sliderAttenuationC.setPaintTicks(true);
		sliderAttenuationC.setPaintLabels(true);
		panel.add(sliderAttenuationC);

		// Attenuation Linear Slider.
		JSlider sliderAttenuationL = new JSlider(JSlider.HORIZONTAL, 0, 500, (int) (light.attenuation.linear * 100.0f));
		sliderAttenuationL.setToolTipText("Attenuation Linear");
		sliderAttenuationL.addChangeListener((ChangeEvent e) -> {
			JSlider source = (JSlider) e.getSource();
			int reading = source.getValue();
			light.attenuation.linear = reading / 100.0f;
		});
		sliderAttenuationL.setMajorTickSpacing(100);
		sliderAttenuationL.setMinorTickSpacing(50);
		sliderAttenuationL.setPaintTicks(true);
		sliderAttenuationL.setPaintLabels(true);
		panel.add(sliderAttenuationL);

		// Attenuation Exponent Slider.
		JSlider sliderAttenuationE = new JSlider(JSlider.HORIZONTAL, 0, 500, (int) (light.attenuation.exponent * 100.0f));
		sliderAttenuationE.setToolTipText("Attenuation Exponent");
		sliderAttenuationE.addChangeListener((ChangeEvent e) -> {
			JSlider source = (JSlider) e.getSource();
			int reading = source.getValue();
			light.attenuation.exponent = reading / 100.0f;
		});
		sliderAttenuationE.setMajorTickSpacing(100);
		sliderAttenuationE.setMinorTickSpacing(50);
		sliderAttenuationE.setPaintTicks(true);
		sliderAttenuationE.setPaintLabels(true);
		panel.add(sliderAttenuationE);

		// X Offset Field.
		JSpinner xOffsetField = new JSpinner(new SpinnerNumberModel((double) offset.x, Double.NEGATIVE_INFINITY + 1.0, Double.POSITIVE_INFINITY - 1.0, 0.1));
		xOffsetField.setToolTipText("Light X Offset");
		xOffsetField.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				offset.x = (float) (double) ((JSpinner) e.getSource()).getValue();
			}
		});
		panel.add(xOffsetField);

		// Y Offset Field.
		JSpinner yOffsetField = new JSpinner(new SpinnerNumberModel((double) offset.x, Double.NEGATIVE_INFINITY + 1.0, Double.POSITIVE_INFINITY - 1.0, 0.1));
		yOffsetField.setToolTipText("Light Y Offset");
		yOffsetField.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				offset.y = (float) (double) ((JSpinner) e.getSource()).getValue();
			}
		});
		panel.add(yOffsetField);

		// Z Offset Field.
		JSpinner zOffsetField = new JSpinner(new SpinnerNumberModel((double) offset.x, Double.NEGATIVE_INFINITY + 1.0, Double.POSITIVE_INFINITY - 1.0, 0.1));
		yOffsetField.setToolTipText("Light Z Offset");
		zOffsetField.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				offset.z = (float) (double) ((JSpinner) e.getSource()).getValue();
			}
		});
		panel.add(zOffsetField);
	}

	@Override
	public void editorUpdate() {
	}

	@Override
	public Pair<String[], String[]> getSaveValues(String entityName) {
		String saveOffset = "new Vector3f(" + offset.x + "f, " + offset.y + "f, " + offset.z + "f)";
		String saveColour = "new Colour(" + light.colour.r + "f, " + light.colour.g + "f, " + light.colour.b + "f)";
		String saveAttenuation = "new Attenuation(" + light.attenuation.constant + "f, " + light.attenuation.linear + "f, " + light.attenuation.exponent + "f)";

		return new Pair<>(
				new String[]{}, // Static variables
				new String[]{saveOffset, saveColour, saveAttenuation} // Class constructor
		);
	}

	@Override
	public void dispose() {
	}
}
