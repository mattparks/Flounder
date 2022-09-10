package com.flounder.physics.bounding;

import com.flounder.devices.*;
import com.flounder.framework.*;
import com.flounder.loaders.*;
import com.flounder.models.*;
import com.flounder.physics.*;
import com.flounder.renderer.FlounderOpenGL;

import java.util.*;

/**
 * A manager for Boundings that want to be renderer.
 */
public class FlounderBounding extends com.flounder.framework.Module {
	private Map<ModelObject, List<Collider>> renderShapes;
	private int boundingCount;

	/**
	 * Creates a new bounding manager.
	 */
	public FlounderBounding() {
		super(FlounderDisplay.class, FlounderMouse.class, FlounderModels.class, FlounderLoader.class);
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
		this.renderShapes = new HashMap<>();
		this.boundingCount = 0;
	}

	@Handler.Function(Handler.FLAG_UPDATE_PRE)
	public void update() {
		boundingCount = renderShapes.size();
		clear(); // Clears before the next batch of rendering.
	}

	/**
	 * Adds a shape to the render pool. (Run every frame).
	 *
	 * @param shape The shape to add.
	 */
	public void addShapeRender(Collider shape) {
		if (!FlounderOpenGL.get().isInWireframe() || shape == null) {
			return;
		}

		for (ModelObject model : renderShapes.keySet()) {
			if (model.equals(shape.getRenderModel())) {
				renderShapes.get(model).add(shape);
				return;
			}
		}

		List<Collider> list = new ArrayList<>();
		list.add(shape);
		renderShapes.put(shape.getRenderModel(), list);
	}

	/**
	 * Gets a list of the renderable shapes.
	 *
	 * @return The renderable shapes.
	 */
	protected Map<ModelObject, List<Collider>> getRenderShapes() {
		return this.renderShapes;
	}

	/**
	 * Clears the renderable Boundings.
	 */
	protected void clear() {
		this.renderShapes.clear();
	}


	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
		clear();
	}

	@com.flounder.framework.Module.Instance
	public static FlounderBounding get() {
		return (FlounderBounding) Framework.get().getModule(FlounderBounding.class);
	}
}
