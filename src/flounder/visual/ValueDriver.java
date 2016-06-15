package flounder.visual;

public abstract class ValueDriver {
	private float length;
	private float currentTime;

	public ValueDriver(float length) {
		currentTime = 0.0f;
		this.length = length;
	}

	public float update(float delta) {
		currentTime += delta;
		currentTime %= length;
		float time = currentTime / length;
		return calculateValue(time);
	}

	protected abstract float calculateValue(float time);
}
