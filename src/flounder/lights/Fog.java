package flounder.lights;

import flounder.maths.*;

/**
 * Represents a fog in the world.
 */
public class Fog {
	private final Colour fogColour;
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
	public Fog(final Colour fogColour, final float fogDensity, final float fogGradient, final float skyLowerLimit, final float skyUpperLimit) {
		this.fogColour = fogColour;
		this.fogDensity = fogDensity;
		this.fogGradient = fogGradient;
		this.skyLowerLimit = skyLowerLimit;
		this.skyUpperLimit = skyUpperLimit;
	}

	public Colour getFogColour() {
		return fogColour;
	}

	public void setFogColour(final Colour fogColour) {
		this.fogColour.set(fogColour);
	}

	public float getFogDensity() {
		return fogDensity;
	}

	public void setFogDensity(final float fogDensity) {
		this.fogDensity = fogDensity;
	}

	public float getFogGradient() {
		return fogGradient;
	}

	public void setFogGradient(final float fogGradient) {
		this.fogGradient = fogGradient;
	}

	public float getSkyLowerLimit() {
		return skyLowerLimit;
	}

	public float getSkyUpperLimit() {
		return skyUpperLimit;
	}
}
