package com.flounder.visual;

import com.flounder.maths.*;

/**
 * A driver that slides to its destination using cosine interpolation.
 */
public class SlideDriver extends ValueDriver {
	private float start;
	private float end;
	private float max;
	private boolean reachedTarget;

	/**
	 * Creates a new slide driver.
	 *
	 * @param start The start value.
	 * @param end The end value.
	 * @param length The time to get to the end value.
	 */
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
