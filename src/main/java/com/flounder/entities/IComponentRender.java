package com.flounder.entities;

import com.flounder.helpers.*;
import com.flounder.shaders.*;

/**
 * Defines a function to be called when rendering the entity.
 */
public interface IComponentRender {
	/**
	 * Called before the entity this is attached to is rendered, is used to loaded shader data and attach images.
	 * If the entity is null the this should clear any data that is not wanted for the entity when the component is absent.
	 * <p>
	 * For example a animation component loads animation matrices and flips animated to true in the shader. If the entity is null animated should be false.
	 *
	 * @param shader The shader to add info to.
	 * @param vaoLength The length of the vao, change if this is a model component.
	 */
	void render(ShaderObject shader, Single<Integer> vaoLength);

	void renderClear(ShaderObject shader);
}
