package flounder.lights;

import flounder.maths.*;
import flounder.maths.vectors.*;

/**
 * Represents a spot light, contains a colour, position and attenuation.
 */
public class LightSpot implements ILight {
	public Colour colour;
	public Vector3f position;
	public Vector3f direction;
	public Attenuation attenuation;
	public float angle;

	public LightSpot(Colour colour, Vector3f position, Vector3f direction, float angle) {
		this(colour, position, direction, new Attenuation(1.0f, 0.0f, 0.0f), angle);
	}

	public LightSpot(Colour colour, Vector3f position, Vector3f direction, Attenuation attenuation, float angle) {
		this.colour = colour;
		this.position = position;
		this.direction = direction;
		this.attenuation = attenuation;
		this.angle = angle;
	}

	@Override
	public String toString() {
		return "LightSpot{" +
				"colour=" + colour +
				", position=" + position +
				", direction=" + direction +
				", attenuation=" + attenuation +
				", angle=" + angle +
				'}';
	}
}
