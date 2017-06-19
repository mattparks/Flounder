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
	public FilterSSAO() {
		super("filterSSAO", new MyFile(PostFilter.POST_LOC, "ssaoFragment.glsl"));
	}

	@Override
	public void storeValues() {
		shader.getUniformMat4("projectionMatrix").loadMat4(FlounderCamera.get().getCamera().getProjectionMatrix());
		shader.getUniformMat4("viewMatrix").loadMat4(FlounderCamera.get().getCamera().getViewMatrix());
		shader.getUniformFloat("aspectRatio").loadFloat(FlounderDisplay.get().getAspectRatio());
		shader.getUniformVec2("texelSize").loadVec2(1.0f / FlounderDisplay.get().getWidth(), 1.0f / FlounderDisplay.get().getHeight());
		shader.getUniformBool("enabled").loadBoolean(!FlounderKeyboard.get().getKey(Constants.GLFW_KEY_O));
	}
}
