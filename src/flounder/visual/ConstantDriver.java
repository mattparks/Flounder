package flounder.visual;

/**
 * A driver that has a constant value.
 */
public class ConstantDriver extends ValueDriver {
	private float value;

	/**
	 * Creates a new constant driver.
	 *
	 * @param constant The constant value.
	 */
	public ConstantDriver(float constant) {
		super(1);
		value = constant;
	}

	@Override
	protected float calculateValue(float time) {
		return value;
	}
}
