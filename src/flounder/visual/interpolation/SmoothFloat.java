package flounder.visual.interpolation;

/**
 * A class that smoothly increases its value.
 */
public class SmoothFloat {
	private float agility;
	private float target;
	private float actual;

	/**
	 * Creates a new smooth float.
	 *
	 * @param initialValue The initial value.
	 * @param agility The agility for increasing actual.
	 */
	public SmoothFloat(float initialValue, float agility) {
		target = initialValue;
		actual = initialValue;
		this.agility = agility;
	}

	/**
	 * Updates the driver with the passed time.
	 *
	 * @param delta The time between the last update.
	 */
	public void update(float delta) {
		float offset = target - actual;
		float change = offset * delta * agility;
		actual += change;
	}

	/**
	 * Sets the target for the smooth float.
	 *
	 * @param target The new target.
	 */
	public void set(float target) {
		this.target = target;
	}

	/**
	 * Gets the smooth floats current target.
	 *
	 * @return The target.
	 */
	public float getTarget(){
		return target;
	}

	/**
	 * Increases the smooth floats target.
	 *
	 * @param increase How much to increase the target by.
	 */
	public void increaseTarget(float increase) {
		target += increase;
	}

	/**
	 * Gets the currently calculated value.
	 *
	 * @return The calculated value.
	 */
	public float get() {
		return actual;
	}
}
