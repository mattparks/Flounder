package flounder.post.filters;

import flounder.post.*;
import flounder.resources.*;

public class FilterTiltShift extends PostFilter {
	private float blurAmount;
	private float centre;
	private float stepSize;
	private float steps;

	public FilterTiltShift() {
		super("filterTiltShift", new MyFile(PostFilter.POST_LOC, "tiltShiftFragment.glsl"));
		this.blurAmount = 1.0f;
		this.centre = 1.1f;
		this.stepSize = 0.004f;
		this.steps = 3.0f;
	}

	public FilterTiltShift(float blurAmount, float centre, float stepSize, float steps) {
		super("filterTiltShift", new MyFile(PostFilter.POST_LOC, "tiltShiftFragment.glsl"));
		this.blurAmount = blurAmount;
		this.centre = centre;
		this.stepSize = stepSize;
		this.steps = steps;
	}

	@Override
	public void storeValues() {
		shader.getUniformFloat("blurAmount").loadFloat(blurAmount);
		shader.getUniformFloat("centre").loadFloat(centre);
		shader.getUniformFloat("stepSize").loadFloat(stepSize);
		shader.getUniformFloat("steps").loadFloat(steps);
	}
}
