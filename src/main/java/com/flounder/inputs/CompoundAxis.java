package com.flounder.inputs;

import com.flounder.maths.*;

/**
 * Axis composed of multiple other axes.
 */
public class CompoundAxis implements IAxis {
	private IAxis[] axes;

	/**
	 * Creates a new axis from a list of axes.
	 *
	 * @param axes The list of axes to combine.
	 */
	public CompoundAxis(IAxis... axes) {
		this.axes = axes;
	}

	@Override
	public float getAmount() {
		float result = 0.0f;

		for (IAxis axe : axes) {
			result += axe.getAmount();
		}

		return Maths.clamp(result, -1.0f, 1.0f);
	}
}
