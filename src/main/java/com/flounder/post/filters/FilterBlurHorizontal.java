package com.flounder.post.filters;

import com.flounder.devices.*;
import com.flounder.fbos.*;
import com.flounder.post.*;
import com.flounder.resources.*;
import com.flounder.shaders.*;

import static com.flounder.platform.Constants.*;

public class FilterBlurHorizontal extends PostFilter {
	private int widthValue;
	private float scaleValue;
	private boolean fitToDisplay;
	private float sizeScalar;

	public FilterBlurHorizontal(float sizeScalar) {
		super(ShaderFactory.newBuilder().setName("filterBlurHorizontal").addType(
				new ShaderType(GL_VERTEX_SHADER, VERTEX_LOCATION)).addType(
				new ShaderType(GL_FRAGMENT_SHADER, new MyFile(PostFilter.POST_LOC, "blurHorizontalFragment.glsl"))
		).create(), FBO.newFBO(sizeScalar).create());
		this.fitToDisplay = true;
		this.sizeScalar = sizeScalar;
		init((int) (FlounderDisplay.get().getWidth() * sizeScalar));
	}

	public FilterBlurHorizontal(int widthValue, int heightValue) {
		super(ShaderFactory.newBuilder().setName("filterBlurHorizontal").addType(
				new ShaderType(GL_VERTEX_SHADER, VERTEX_LOCATION)).addType(
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
			widthValue = (int) (FlounderDisplay.get().getWidth() * sizeScalar);
		}

		shader.getUniformFloat("width").loadFloat(widthValue);
		shader.getUniformFloat("scale").loadFloat(scaleValue);
	}
}
