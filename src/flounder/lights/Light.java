package flounder.lights;

import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.physics.*;
import flounder.space.*;

/**
 * Represents a light in the game, contains a colour, position and attenuation.
 */
public class Light implements ISpatialObject {
	public Colour colour;
	public Vector3f position;
	public Attenuation attenuation;
	private Sphere lightDistance;

	/**
	 * Creates a new Light with unlimited range.
	 *
	 * @param colour The colour of the light.
	 * @param position The world position of the light.
	 */
	public Light(Colour colour, Vector3f position) {
		this(colour, position, new Attenuation(1.0f, 0.0f, 0.0f));
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
		this.lightDistance = new Sphere((float) quadratic(attenuation.constant, attenuation.linear, attenuation.exponent), position);
	}

	private double quadratic(double a, double b, double c) {
		return (-b + Math.sqrt((b * b) + (4.0 * a * c))) / (2.0 * a);
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
	public Sphere getBounding() {
		return lightDistance;
	}

	@Override
	public String toString() {
		return "Light{" +
				"colour=" + colour +
				", position=" + position +
				", attenuation=" + attenuation +
				", lightDistance=" + lightDistance +
				'}';
	}
}
