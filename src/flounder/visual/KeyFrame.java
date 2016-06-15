package flounder.visual;

public class KeyFrame {
	private float value;
	private float time;

	public KeyFrame(float time, float value) {
		this.time = time;
		this.value = value;
	}

	protected float getValue() {
		return value;
	}

	protected void setValue(float value) {
		this.value = value;
	}

	protected float getTime() {
		return time;
	}

	protected void setTime(float time) {
		this.time = time;
	}
}
