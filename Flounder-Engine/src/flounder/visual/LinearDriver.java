package flounder.visual;

/**
 * A driver that linearly increases its value.
 */
public class LinearDriver extends ValueDriver {
	private float startValue;
	private float difference;

	/**
	 * Creates a new linear driver.
	 *
	 * @param startValue The start value.
	 * @param endValue The end value.
	 * @param length The time to go between values.
	 */
	public LinearDriver(float startValue, float endValue, float length) {
		super(length);
		this.startValue = startValue;
		difference = endValue - startValue;
	}

	@Override
	protected float calculateValue(float time) {
		return startValue + time * difference;
	}
}
