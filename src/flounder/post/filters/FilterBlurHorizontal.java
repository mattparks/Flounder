package flounder.post.filters;

import flounder.devices.*;
import flounder.fbos.*;
import flounder.post.*;
import flounder.resources.*;
import flounder.shaders.*;

import static org.lwjgl.opengl.GL20.*;

public class FilterBlurHorizontal extends PostFilter {
	private int widthValue;
	private float scaleValue;
	private boolean fitToDisplay;
	private float sizeScalar;

	public FilterBlurHorizontal(float sizeScalar) {
		super(Shader.newShader("filterBlurHorizontal").setShaderTypes(
				new ShaderType(GL_VERTEX_SHADER, VERTEX_LOCATION),
				new ShaderType(GL_FRAGMENT_SHADER, new MyFile(PostFilter.POST_LOC, "blurHorizontalFragment.glsl"))
		).create(), FBO.newFBO(sizeScalar).create());
		this.fitToDisplay = true;
		this.sizeScalar = sizeScalar;
		init((int) (FlounderDisplay.getWidth() * sizeScalar));
	}

	public FilterBlurHorizontal(int widthValue, int heightValue) {
		super(Shader.newShader("filterBlurHorizontal").setShaderTypes(
				new ShaderType(GL_VERTEX_SHADER, VERTEX_LOCATION),
				new ShaderType(GL_FRAGMENT_SHADER, new MyFile(PostFilter.POST_LOC, "blurHorizontalFragment.glsl"))
		).create(), FBO.newFBO(widthValue, heightValue).create());
		this.fitToDisplay = false;
		this.sizeScalar = 1.0f;
		init(widthValue);
	}

	private void init(int widthValue) {
		this.widthValue = widthValue;
		this.scaleValue = 2.0f;
	}

	public void setScale(float scale) {
		this.scaleValue = scale;
	}

	@Override
	public void storeValues() {
		if (fitToDisplay) {
			widthValue = (int) (FlounderDisplay.getWidth() * sizeScalar);
		}

		shader.getUniformFloat("width").loadFloat(widthValue);
		shader.getUniformFloat("scale").loadFloat(scaleValue);
	}
}
