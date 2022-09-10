package com.flounder.post.filters;

import com.flounder.camera.*;
import com.flounder.entities.*;
import com.flounder.entities.components.*;
import com.flounder.fbos.*;
import com.flounder.post.*;
import com.flounder.resources.*;
import com.flounder.shadows.*;
import com.flounder.skybox.*;

public class FilterMRT extends PostFilter {
	private static final int LIGHTS = 64;

	public FilterMRT() {
		super("filterMRT", new MyFile(PostFilter.POST_LOC, "mrtFragment.glsl"));
	}

	public FilterMRT(FBO fbo) {
		super("filterMRT", new MyFile(PostFilter.POST_LOC, "mrtFragment.glsl"), fbo);
	}

	@Override
	public void storeValues() {
		shader.getUniformMat4("projectionMatrix").loadMat4(FlounderCamera.get().getCamera().getProjectionMatrix());
		shader.getUniformMat4("viewMatrix").loadMat4(FlounderCamera.get().getCamera().getViewMatrix());

		int lightsLoaded = 0;

		if (FlounderEntities.get().getEntities() != null) {
			for (Entity entity : FlounderEntities.get().getEntities().getAll(null)) {
				ComponentLight componentLight = (ComponentLight) entity.getComponent(ComponentLight.class);

				if (lightsLoaded < LIGHTS && componentLight != null) {
					shader.getUniformBool("lightActive[" + lightsLoaded + "]").loadBoolean(true);
					shader.getUniformVec3("lightColour[" + lightsLoaded + "]").loadVec3(componentLight.getLight().getColour());
					shader.getUniformVec3("lightPosition[" + lightsLoaded + "]").loadVec3(componentLight.getLight().getPosition());
					shader.getUniformVec3("lightAttenuation[" + lightsLoaded + "]").loadVec3(componentLight.getLight().getAttenuation());
					lightsLoaded++;
				}
			}
		}

		if (lightsLoaded < LIGHTS) {
			for (int i = lightsLoaded; i < LIGHTS; i++) {
				shader.getUniformBool("lightActive[" + i + "]").loadBoolean(false);
				shader.getUniformVec3("lightColour[" + i + "]").loadVec3(0.0f, 0.0f, 0.0f);
				shader.getUniformVec3("lightPosition[" + i + "]").loadVec3(0.0f, 0.0f, 0.0f);
				shader.getUniformVec3("lightAttenuation[" + i + "]").loadVec3(1.0f, 0.0f, 0.0f);
			}
		}

		shader.getUniformMat4("shadowSpaceMatrix").loadMat4(FlounderShadows.get().getToShadowMapSpaceMatrix());
		shader.getUniformFloat("shadowDistance").loadFloat(FlounderShadows.get().getShadowBoxDistance());
		shader.getUniformFloat("shadowTransition").loadFloat(FlounderShadows.get().getShadowTransition());
		shader.getUniformInt("shadowMapSize").loadInt(FlounderShadows.get().getShadowSize());
		shader.getUniformInt("shadowPCF").loadInt(FlounderShadows.get().getShadowPCF());
		shader.getUniformFloat("shadowBias").loadFloat(FlounderShadows.get().getShadowBias());
		shader.getUniformFloat("shadowDarkness").loadFloat(FlounderShadows.get().getShadowDarkness() * FlounderShadows.get().getShadowFactor());

		shader.getUniformFloat("brightnessBoost").loadFloat(FlounderShadows.get().getBrightnessBoost());

		if (FlounderSkybox.get().getFog() != null) {
			shader.getUniformVec3("fogColour").loadVec3(FlounderSkybox.get().getFog().getFogColour());
			shader.getUniformFloat("fogDensity").loadFloat(FlounderSkybox.get().getFog().getFogDensity());
			shader.getUniformFloat("fogGradient").loadFloat(FlounderSkybox.get().getFog().getFogGradient());
		} else {
			shader.getUniformVec3("fogColour").loadVec3(1.0f, 1.0f, 1.0f);
			shader.getUniformFloat("fogDensity").loadFloat(0.003f);
			shader.getUniformFloat("fogGradient").loadFloat(2.0f);
		}
	}
}
