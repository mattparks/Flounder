package flounder.lights;

import flounder.maths.*;
import flounder.maths.vectors.*;

/**
 * Represents a spot light, contains a colour, position and attenuation.
 */
public class LightSpot implements ILight {
	public Colour colour;
	public Vector3f position;
	public Vector3f coneDirection;
	public Attenuation attenuation;
	public float coneAngle;

	public LightSpot(Colour colour, Vector3f position, Vector3f coneDirection, float coneAngle) {
		this(colour, position, coneDirection, new Attenuation(1.0f, 0.0f, 0.0f), coneAngle);
	}

	public LightSpot(Colour colour, Vector3f position, Vector3f coneDirection, Attenuation attenuation, float coneAngle) {
		this.colour = colour;
		this.position = position;
		this.coneDirection = coneDirection;
		this.attenuation = attenuation;
		this.coneAngle = coneAngle;
	}

	@Override
	public String toString() {
		return "LightSpot{" +
				"colour=" + colour +
				", position=" + position +
				", coneDirection=" + coneDirection +
				", attenuation=" + attenuation +
				", coneAngle=" + coneAngle +
				'}';
	}
}
