package com.flounder.renderer;

import com.flounder.camera.*;
import com.flounder.maths.vectors.*;

/**
 * Represents a sub renderer in the engine.
 */
public abstract class Renderer {
	/**
	 * Creates a new sub renderer in the engine.
	 */
	public Renderer() {
	}

	/**
	 * Called when the renderer is needed to be rendered.
	 *
	 * @param clipPlane The current clip plane.
	 * @param camera The camera to be used when rendering.
	 */
	public abstract void render(Vector4f clipPlane, Camera camera);

	/**
	 * Cleans up all of the renderers processes.
	 */
	public abstract void dispose();
}
