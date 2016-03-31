package flounder.inputs;

/**
 * Axis composed of two buttons.
 */
public class ButtonAxis implements IAxis {
	private final IButton positive;
	private final IButton negative;

	/**
	 * Creates an axis from two buttons.
	 *
	 * @param negative When this button is down, the axis is negative.
	 * @param positive When this button is down, the axis is positive.
	 */
	public ButtonAxis(final IButton negative, final IButton positive) {
		this.negative = negative;
		this.positive = positive;
	}

	@Override
	public float getAmount() {
		float result = 0.0f;

		if (positive.isDown()) {
			result += 1.0f;
		}

		if (negative.isDown()) {
			result -= 1.0f;
		}

		return result;
	}
}
