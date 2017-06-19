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
import flounder.logger.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.post.*;
import flounder.resources.*;

public class FilterSSAO extends PostFilter {
	public FilterSSAO() {
		super("filterSSAO", new MyFile(PostFilter.POST_LOC, "ssaoFragment.glsl"));
	}

	@Override
	public void storeValues() {
		shader.getUniformMat4("projectionMatrix").loadMat4(FlounderCamera.get().getCamera().getProjectionMatrix());
		shader.getUniformMat4("viewMatrix").loadMat4(FlounderCamera.get().getCamera().getViewMatrix());
		shader.getUniformFloat("aspectRatio").loadFloat(FlounderDisplay.get().getAspectRatio());
	}
}
