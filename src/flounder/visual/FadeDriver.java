package flounder.visual;

public class FadeDriver extends ValueDriver {
	private float start;
	private float end;
	private float peak;

	public FadeDriver(float peak, float start, float end, float duration) {
		super(duration);
		this.peak = peak;
		this.start = start;
		this.end = end;
	}

	public float getStart() {
		return start;
	}

	public void setStart(float start) {
		this.start = start;
	}

	public float getEnd() {
		return end;
	}

	public void setEnd(float end) {
		this.end = end;
	}

	public float getPeak() {
		return peak;
	}

	public void setPeak(float peak) {
		this.peak = peak;
	}

	@Override
	protected float calculateValue(float time) {
		if (time < start) {
			return time / start * peak;
		} else if (time > end) {
			return (1 - (time - end) / (1 - end)) * peak;
		} else {
			return peak;
		}
	}
}
