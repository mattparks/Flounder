package flounder.post.filters;

import flounder.post.*;
import flounder.resources.*;

public class FilterCRT extends PostFilter {
	private float curveAmountX;
	private float curveAmountY;
	private float scanLineSize;

	public FilterCRT() {
		super("filterCrt", new MyFile(PostFilter.POST_LOC, "crtFragment.glsl"));
		this.curveAmountX = 0.175f;
		this.curveAmountY = 0.175f;
		this.scanLineSize = 920.0f;
	}

	public FilterCRT(float curveAmountX, float curveAmountY, float scanLineSize) {
		super("filterCrt", new MyFile(PostFilter.POST_LOC, "crtFragment.glsl"));
		this.curveAmountX = curveAmountX;
		this.curveAmountY = curveAmountY;
		this.scanLineSize = scanLineSize;
	}

	@Override
	public void storeValues() {
		shader.getUniformFloat("curveAmountX").loadFloat(curveAmountX);
		shader.getUniformFloat("curveAmountY").loadFloat(curveAmountY);
		shader.getUniformFloat("scanLineSize").loadFloat(scanLineSize);
	}
}
