package flounder.post.filters;

import flounder.camera.*;
import flounder.entities.*;
import flounder.entities.components.*;
import flounder.fbos.*;
import flounder.post.*;
import flounder.resources.*;
import flounder.shadows.*;
import flounder.skybox.*;

import java.util.*;

public class FilterMRT extends PostFilter {
	private static final int LIGHTS = 64;
	private float shadowFactor;

	public FilterMRT() {
		super("filterMrt", new MyFile(PostFilter.POST_LOC, "mrtFragment.glsl"));
		this.shadowFactor = 1.0f;
	}

	public FilterMRT(FBO fbo) {
		super("filterMrt", new MyFile(PostFilter.POST_LOC, "mrtFragment.glsl"), fbo);
		this.shadowFactor = 1.0f;
	}

	public void setShadowFactor(float shadowFactor) {
		this.shadowFactor = shadowFactor;
	}

	@Override
	public void storeValues() {
		shader.getUniformMat4("projectionMatrix").loadMat4(FlounderCamera.getCamera().getProjectionMatrix());
		shader.getUniformMat4("viewMatrix").loadMat4(FlounderCamera.getCamera().getViewMatrix());

		int lightsLoaded = 0;

		if (FlounderEntities.getEntities() != null) {
			for (Entity entity : new ArrayList<>(FlounderEntities.getEntities().getAll())) {
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

		// FlounderProfiler.get().add(KosmosPost.PROFILE_TAB_NAME, "Maximum Lights", LIGHTS);
		// FlounderProfiler.get().add(KosmosPost.PROFILE_TAB_NAME, "Loaded Lights", lightsLoaded);

		if (lightsLoaded < LIGHTS) {
			for (int i = lightsLoaded; i < LIGHTS; i++) {
				shader.getUniformVec3("lightColour[" + i + "]").loadVec3(0.0f, 0.0f, 0.0f);
				shader.getUniformVec3("lightPosition[" + i + "]").loadVec3(0.0f, 0.0f, 0.0f);
				shader.getUniformVec3("lightAttenuation[" + i + "]").loadVec3(1.0f, 0.0f, 0.0f);
			}
		}

		shader.getUniformMat4("shadowSpaceMatrix").loadMat4(FlounderShadows.getToShadowMapSpaceMatrix());
		shader.getUniformFloat("shadowDistance").loadFloat(FlounderShadows.getShadowBoxDistance());
		shader.getUniformFloat("shadowTransition").loadFloat(FlounderShadows.getShadowTransition());
		shader.getUniformInt("shadowMapSize").loadInt(FlounderShadows.getShadowSize());
		shader.getUniformInt("shadowPCF").loadInt(FlounderShadows.getShadowPCF());
		shader.getUniformFloat("shadowBias").loadFloat(FlounderShadows.getShadowBias());
		shader.getUniformFloat("shadowDarkness").loadFloat(FlounderShadows.getShadowDarkness() * shadowFactor);

		shader.getUniformFloat("brightnessBoost").loadFloat(FlounderShadows.getBrightnessBoost());

		if (FlounderSkybox.getFog() != null) {
			shader.getUniformVec3("fogColour").loadVec3(FlounderSkybox.getFog().getFogColour());
			shader.getUniformFloat("fogDensity").loadFloat(FlounderSkybox.getFog().getFogDensity());
			shader.getUniformFloat("fogGradient").loadFloat(FlounderSkybox.getFog().getFogGradient());
		} else {
			shader.getUniformVec3("fogColour").loadVec3(1.0f, 1.0f, 1.0f);
			shader.getUniformFloat("fogDensity").loadFloat(0.003f);
			shader.getUniformFloat("fogGradient").loadFloat(2.0f);
		}
	}
}
