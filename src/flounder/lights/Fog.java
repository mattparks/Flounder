package flounder.lights;

import flounder.maths.*;

/**
 * Represents a fog in the world.
 */
public class Fog {
	private Colour fogColour;
	private float fogDensity;
	private float fogGradient;
	private float skyLowerLimit;
	private float skyUpperLimit;

	/**
	 * Creates a new fog.
	 *
	 * @param fogColour The colour of the fog.
	 * @param fogDensity How dense the fog will be.
	 * @param fogGradient The gradient of the fog.
	 * @param skyLowerLimit At what height will the skybox fog begin to appear.
	 * @param skyUpperLimit At what height will there be skybox no fog.
	 */
	public Fog(Colour fogColour, float fogDensity, float fogGradient, float skyLowerLimit, float skyUpperLimit) {
		this.fogColour = fogColour;
		this.fogDensity = fogDensity;
		this.fogGradient = fogGradient;
		this.skyLowerLimit = skyLowerLimit;
		this.skyUpperLimit = skyUpperLimit;
	}

	/**
	 * Gets the fogs colour.
	 *
	 * @return The fog colour.
	 */
	public Colour getFogColour() {
		return fogColour;
	}

	/**
	 * Sets the fogs colour.
	 *
	 * @param fogColour The new fog colour.
	 */
	public void setFogColour(Colour fogColour) {
		this.fogColour.set(fogColour);
	}

	/**
	 * Gets the fogs density.
	 *
	 * @return The fog density.
	 */
	public float getFogDensity() {
		return fogDensity;
	}

	/**
	 * Sets the fogs density.
	 *
	 * @param fogDensity The fogs new density.
	 */
	public void setFogDensity(float fogDensity) {
		this.fogDensity = fogDensity;
	}

	/**
	 * Gets the fogs gradient.
	 *
	 * @return The fog gradient.
	 */
	public float getFogGradient() {
		return fogGradient;
	}

	/**
	 * Sets the fogs gradient.
	 *
	 * @param fogGradient The fogs new gradient.
	 */
	public void setFogGradient(float fogGradient) {
		this.fogGradient = fogGradient;
	}

	/**
	 * Gets the skys lower limit.
	 *
	 * @return The skys lower limit.
	 */
	public float getSkyLowerLimit() {
		return skyLowerLimit;
	}

	/**
	 * Gets the skys upper limit.
	 *
	 * @return The skys upper limit.
	 */
	public float getSkyUpperLimit() {
		return skyUpperLimit;
	}
}
