package flounder.lights;

import flounder.maths.*;
import flounder.maths.vectors.*;

/**
 * Represents a point light, contains a colour, position and attenuation.
 */
public class Light {
	public Colour colour;
	public Vector3f position;
	public Attenuation attenuation;

	/**
	 * Creates a new point light with unlimited range.
	 *
	 * @param colour The colour of the light.
	 * @param position The world position of the light.
	 */
	public Light(Colour colour, Vector3f position) {
		this(colour, position, new Attenuation(1.0f, 0.0f, 0.0f));
	}

	/**
	 * Creates a new point light.
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

	/**
	 * Gets the lights colour.
	 *
	 * @return The lights colour.
	 */
	public Colour getColour() {
		return colour;
	}

	/**
	 * Sets the lights colour.
	 *
	 * @param colour The new light colour.
	 */
	public void setColour(Colour colour) {
		this.colour = colour;
	}

	/**
	 * Gets the lights position.
	 *
	 * @return The lights position.
	 */
	public Vector3f getPosition() {
		return position;
	}

	/**
	 * Sets the lights position.
	 *
	 * @param position The new light position.
	 */
	public void setPosition(Vector3f position) {
		this.position = position;
	}

	/**
	 * Gets the light attenuation.
	 *
	 * @return The light attenuation.
	 */
	public Attenuation getAttenuation() {
		return attenuation;
	}

	/**
	 * Sets the light attenuation.
	 *
	 * @param attenuation The new light attenuation.
	 */
	public void setAttenuation(Attenuation attenuation) {
		this.attenuation = attenuation;
	}

	@Override
	public String toString() {
		return "Light{" +
				"colour=" + colour +
				", position=" + position +
				", attenuation=" + attenuation +
				'}';
	}
}
