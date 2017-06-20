/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved.
 *
 * This source file is part of New Kosmos.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package flounder.post.filters;

import flounder.camera.*;
import flounder.devices.*;
import flounder.fbos.*;
import flounder.helpers.*;
import flounder.logger.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.platform.*;
import flounder.post.*;
import flounder.resources.*;
import flounder.textures.*;

public class FilterSSAO extends PostFilter {
	private static final int KERNEL_SIZE = 64;

	private Vector3f[] kernel;

	public FilterSSAO() {
		super("filterSSAO", new MyFile(PostFilter.POST_LOC, "ssaoFragment.glsl"));

		this.kernel = new Vector3f[KERNEL_SIZE];

		for (int i = 0; i < KERNEL_SIZE; i++) {
			Vector3f sample = new Vector3f(
					Maths.randomInRange(0.0f, 1.0f) * 2.0f - 1.0f,
					Maths.randomInRange(0.0f, 1.0f) * 2.0f - 1.0f,
					Maths.randomInRange(0.0f, 1.0f)
			);
			sample.normalize();
			sample.scale(Maths.randomInRange(0.0f, 1.0f));
			float scale = (float)i / KERNEL_SIZE;
			scale = lerp(0.1f, 1.0f, scale * scale);
			sample.scale(scale);
			kernel[i] = sample;
		}
	}

	private float lerp(float a, float b, float f) {
		return a + f * (b - a);
	}

	@Override
	public void storeValues() {
		shader.getUniformMat4("projectionMatrix").loadMat4(FlounderCamera.get().getCamera().getProjectionMatrix());
		shader.getUniformMat4("viewMatrix").loadMat4(FlounderCamera.get().getCamera().getViewMatrix());
		shader.getUniformFloat("aspectRatio").loadFloat(FlounderDisplay.get().getAspectRatio());

		for (int i = 0; i < KERNEL_SIZE; i++) {
			shader.getUniformVec3("kernel[" + i + "]").loadVec3(kernel[i]);
		}

		shader.getUniformBool("enabled").loadBoolean(!FlounderKeyboard.get().getKey(Constants.GLFW_KEY_O));
	}
}
