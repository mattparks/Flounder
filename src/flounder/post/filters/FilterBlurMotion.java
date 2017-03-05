package flounder.post.filters;

import flounder.camera.*;
import flounder.framework.*;
import flounder.maths.matrices.*;
import flounder.post.*;
import flounder.resources.*;

public class FilterBlurMotion extends PostFilter {
	private Matrix4f lastViewMatrix;

	public FilterBlurMotion() {
		super("filterBlurMotion", new MyFile(PostFilter.POST_LOC, "blurMotionFragment.glsl"));
		this.lastViewMatrix = new Matrix4f();

		// Initial last view matrix.
		if (FlounderCamera.getCamera() != null) {
			this.lastViewMatrix.set(FlounderCamera.getCamera().getViewMatrix());
		}
	}

	@Override
	public void storeValues() {
		shader.getUniformMat4("projectionMatrix").loadMat4(FlounderCamera.getCamera().getProjectionMatrix());
		shader.getUniformMat4("viewMatrix").loadMat4(FlounderCamera.getCamera().getViewMatrix());
		shader.getUniformMat4("lastViewMatrix").loadMat4(lastViewMatrix);
		shader.getUniformFloat("delta").loadFloat(Framework.getDelta()); // Render delta?
		this.lastViewMatrix.set(FlounderCamera.getCamera().getViewMatrix());
	}
}
