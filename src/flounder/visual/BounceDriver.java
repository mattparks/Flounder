package flounder.visual;

import flounder.framework.*;

/**
 * A bounce driver that uses a sine wave.
 */
public class BounceDriver extends ValueDriver {
	private float start;
	private float amplitude;
	private float length;
	private float actime;

	/**
	 * Creates a new sine wave driver.
	 *
	 * @param start The start value.
	 * @param end The end value.
	 * @param length The length between two waves.
	 */
	public BounceDriver(float start, float end, float length) {
		super(length);
		this.start = start;
		this.amplitude = end - start;
		this.length = length;
		this.actime = 0.0f;
	}

	@Override
	protected float calculateValue(float time) {
		float value = 0.5f + (float) Math.sin(time * Math.PI * 2.0) * 0.5f;
		actime += Framework.getDelta();

		if (actime > length / 2.0f) {
			value = 0.0f;
		}

		return start + value * amplitude;
	}
}
