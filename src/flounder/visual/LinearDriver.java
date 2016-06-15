package flounder.visual;

public class LinearDriver extends ValueDriver {
	private float startValue;
	private float difference;

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
