package flounder.visual;

public class ConstantDriver extends ValueDriver {
	private float value;

	public ConstantDriver(float constant) {
		super(1);
		value = constant;
	}

	@Override
	protected float calculateValue(float time) {
		return value;
	}
}
