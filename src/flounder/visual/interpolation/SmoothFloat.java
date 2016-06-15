package flounder.visual.interpolation;

public class SmoothFloat {
	private float agility;
	private float target;
	private float actual;

	public SmoothFloat(float initialValue, float agility) {
		target = initialValue;
		actual = initialValue;
		this.agility = agility;
	}

	public void update(float delta) {
		float offset = target - actual;
		float change = offset * delta * agility;
		actual += change;
	}

	public void set(float target) {
		this.target = target;
	}

	public void instantIncrease(float increase) {
		actual += increase;
	}

	public float get() {
		return actual;
	}
}
