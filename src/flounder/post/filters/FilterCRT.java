package flounder.post.filters;

import flounder.devices.*;
import flounder.framework.*;
import flounder.maths.*;
import flounder.post.*;
import flounder.resources.*;

public class FilterCRT extends PostFilter {
	private Colour screenColour;
	private float curveAmountX;
	private float curveAmountY;
	private float scanLineSize;
	private float scanIntensity;

	public FilterCRT() {
		super("filterCrt", new MyFile(PostFilter.POST_LOC, "crtFragment.glsl"));
		this.screenColour = new Colour(0.3f, 1.0f, 0.3f);
		this.curveAmountX = 0.175f;
		this.curveAmountY = 0.175f;
		this.scanLineSize = 920.0f;
		this.scanIntensity = 0.02f;
	}

	public FilterCRT(Colour screenColour, float curveAmountX, float curveAmountY, float scanLineSize, float scanIntensity) {
		super("filterCrt", new MyFile(PostFilter.POST_LOC, "crtFragment.glsl"));
		this.screenColour = screenColour;
		this.curveAmountX = curveAmountX;
		this.curveAmountY = curveAmountY;
		this.scanLineSize = scanLineSize;
		this.scanIntensity = scanIntensity;
	}

	@Override
	public void storeValues() {
		shader.getUniformVec3("screenColour").loadVec3(screenColour);
		shader.getUniformFloat("curveAmountX").loadFloat(curveAmountX * FlounderDisplay.get().getAspectRatio());
		shader.getUniformFloat("curveAmountY").loadFloat(curveAmountY);
		shader.getUniformFloat("scanLineSize").loadFloat(scanLineSize);
		shader.getUniformFloat("scanIntensity").loadFloat(scanIntensity);

		shader.getUniformFloat("moveTime").loadFloat(Framework.get().getTimeSec() / 100.0f);
	}
}
