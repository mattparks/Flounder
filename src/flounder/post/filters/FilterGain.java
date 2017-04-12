package flounder.post.filters;

import flounder.framework.*;
import flounder.post.*;
import flounder.resources.*;

public class FilterGain extends PostFilter {
	private boolean moving;
	private float strength;

	public FilterGain(boolean moving, float strength) {
		super("filterGrain", new MyFile(PostFilter.POST_LOC, "grainFragment.glsl"));
		this.moving = moving;
		this.strength = strength;
	}

	@Override
	public void storeValues() {
		shader.getUniformFloat("time").loadFloat(moving ? Framework.getTimeSec() : 1.0f);
		shader.getUniformFloat("strength").loadFloat(strength);
	}

	public boolean isMoving() {
		return moving;
	}

	public void setMoving(boolean moving) {
		this.moving = moving;
	}

	public float getStrength() {
		return strength;
	}

	public void setStrength(float strength) {
		this.strength = strength;
	}
}
