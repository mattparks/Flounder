package flounder.lights;

import flounder.maths.*;
import flounder.maths.vectors.*;

/**
 * Represents a light in the game, contains a colour, position and attenuation.
 */
public class Light {
	private Colour colour;
	private Vector3f position;
	private Attenuation attenuation;

	/**
	 * Creates a new Light with unlimited range.
	 *
	 * @param colour The colour of the light.
	 * @param position The world position of the light.
	 */
	public Light(Colour colour, Vector3f position) {
		this(colour, position, new Attenuation(1, 0, 0));
	}

	/**
	 * Creates a new Light.
	 *
	 * @param colour The colour of the light.
	 * @param position The world position of the light.
	 * @param attenuation How much the intensity of the light is lost over a distance.
	 */
	public Light(Colour colour, Vector3f position, Attenuation attenuation) {
		this.colour = colour;
		this.position = position;
		this.attenuation = attenuation;
	}

	public Colour getColour() {
		return colour;
	}

	public void setColour(Colour colour) {
		this.colour = colour;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Attenuation getAttenuation() {
		return attenuation;
	}

	public void setAttenuation(Attenuation attenuation) {
		this.attenuation = attenuation;
	}
}
