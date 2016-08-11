package flounder.lights;

/**
 * Attenuation is used in calculating the range of engine.flounder.lights.
 */
public class Attenuation {
	public float constant;
	public float linear;
	public float exponent;

	/**
	 * Creates a Attenuation object used in engine.flounder.lights. The calculation used is as follows:<br>
	 * {@code factor = constant + (linear * cameraDistance) + (exponent * (cameraDistance * cameraDistance))}
	 *
	 * @param constant The constant Attenuation value.
	 * @param linear The linear Attenuation value.
	 * @param exponent The exponent Attenuation value.
	 */
	public Attenuation(float constant, float linear, float exponent) {
		this.constant = constant;
		this.linear = linear;
		this.exponent = exponent;
	}

	/**
	 * Gets the constant value.
	 *
	 * @return The constant value.
	 */
	public float getConstant() {
		return constant;
	}

	/**
	 * Gets the constant value.
	 *
	 * @param constant The new constant.
	 */
	public void setConstant(float constant) {
		this.constant = constant;
	}

	/**
	 * Gets the linear value.
	 *
	 * @return The linear value.
	 */
	public float getLinear() {
		return linear;
	}

	/**
	 * Gets the linear value.
	 *
	 * @param linear The new linear.
	 */
	public void setLinear(float linear) {
		this.linear = linear;
	}

	/**
	 * Gets the exponent value.
	 *
	 * @return The exponent value.
	 */
	public float getExponent() {
		return exponent;
	}

	/**
	 * Gets the exponent value.
	 *
	 * @param exponent The new exponent.
	 */
	public void setExponent(float exponent) {
		this.exponent = exponent;
	}

	/**
	 * Get's the max distance the light can effect, not NaN is returned by lights with infinite influence.
	 *
	 * @return The lights max distance
	 */
	public float getDistance() {
		return (float) Math.abs((-Math.sqrt((linear * linear) + (4 * exponent * constant)) - linear) / (2 * exponent));
	}
}
