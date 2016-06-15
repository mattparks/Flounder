package flounder.visual;

import flounder.maths.*;

public class SlideDriver extends ValueDriver {
	private float start;
	private float end;
	private float max;
	private boolean reachedTarget;

	public SlideDriver(float start, float end, float length) {
		super(length);
		this.start = start;
		this.end = end;
		max = 0.0f;
		reachedTarget = false;
	}

	@Override
	protected float calculateValue(float time) {
		if (!reachedTarget && time >= max) {
			max = time;
			return Maths.cosInterpolate(start, end, time);
		} else {
			reachedTarget = true;
			return start + (end - start);
		}
	}
}
