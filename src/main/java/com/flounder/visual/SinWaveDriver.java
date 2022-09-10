package com.flounder.visual;

/**
 * A driver that uses a sine wave.
 */
public class SinWaveDriver extends ValueDriver {
	private float min;
	private float amplitude;

	/**
	 * Creates a new sine wave driver.
	 *
	 * @param min The min value.
	 * @param max The max value.
	 * @param length The length between two waves.
	 */
	public SinWaveDriver(float min, float max, float length) {
		super(length);
		this.min = min;
		this.amplitude = max - min;
	}

	@Override
	protected float calculateValue(float time) {
		float value = 0.5f + (float) Math.sin(time * Math.PI * 2.0) * 0.5f;
		return min + value * amplitude;
	}
}
