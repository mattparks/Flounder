package flounder.visual;

/**
 * A driver that has a variable value.
 */
public class VarianceDriver extends ValueDriver {
	private float value;

	/**
	 * Creates a new variable driver.
	 *
	 * @param variable The variable value.
	 */
	public VarianceDriver(float variable) {
		super(1);
		value = variable;
	}

	@Override
	protected float calculateValue(float time) {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public static void set(ValueDriver driver, float value) {
		if (driver instanceof VarianceDriver) {
			((VarianceDriver) driver).setValue(value);
		}
	}
}
