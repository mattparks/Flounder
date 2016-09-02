package flounder.post.filters;

import flounder.engine.*;
import flounder.fbos.*;
import flounder.post.*;
import flounder.resources.*;
import flounder.shaders.*;

import static org.lwjgl.opengl.GL20.*;

public class FilterBlurVertical extends PostFilter {
	private int heightValue;
	private float scaleValue;
	private boolean fitToDisplay;
	private float sizeScalar;

	public FilterBlurVertical(float sizeScalar) {
		super(Shader.newShader("filterBlurVertical").setShaderTypes(
				new ShaderType(GL_VERTEX_SHADER, VERTEX_LOCATION),
				new ShaderType(GL_FRAGMENT_SHADER, new MyFile(PostFilter.POST_LOC, "blurVerticalFragment.glsl"))
		).createInSecondThread(), FBO.newFBO(FlounderEngine.getDevices().getDisplay().getWidth(), FlounderEngine.getDevices().getDisplay().getHeight()).fitToScreen(1.0f).create());
		fitToDisplay = true;
		this.sizeScalar = sizeScalar;
		init((int) (FlounderEngine.getDevices().getDisplay().getHeight() * sizeScalar));
	}

	public FilterBlurVertical(int widthValue, int heightValue) {
		super(Shader.newShader("filterBlurVertical").setShaderTypes(
				new ShaderType(GL_VERTEX_SHADER, VERTEX_LOCATION),
				new ShaderType(GL_FRAGMENT_SHADER, new MyFile(PostFilter.POST_LOC, "blurVerticalFragment.glsl"))
		).createInSecondThread(), FBO.newFBO(widthValue, heightValue).create());
		fitToDisplay = false;
		this.sizeScalar = 1.0f;
		init(heightValue);
	}

	private void init(int heightValue) {
		this.heightValue = heightValue;
		this.scaleValue = 2.0f;
	}

	public void setScale(float scale) {
		this.scaleValue = scale;
	}

	@Override
	public void storeValues() {
		if (fitToDisplay) {
			heightValue = (int) (FlounderEngine.getDevices().getDisplay().getHeight() * sizeScalar);
		}

		shader.getUniformFloat("height").loadFloat(heightValue);
		shader.getUniformFloat("scale").loadFloat(scaleValue);
	}
}
