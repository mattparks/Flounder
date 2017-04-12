package flounder.post.filters;

import flounder.post.*;
import flounder.resources.*;

public class FilterGain extends PostFilter {
	private float strength;

	public FilterGain(float strength) {
		super("filterGrain", new MyFile(PostFilter.POST_LOC, "grainFragment.glsl"));
		this.strength = strength;
	}

	@Override
	public void storeValues() {
		shader.getUniformFloat("strength").loadFloat(strength);
	}

	public float getStrength() {
		return strength;
	}

	public void setStrength(float strength) {
		this.strength = strength;
	}
}
