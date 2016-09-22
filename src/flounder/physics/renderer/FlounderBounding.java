package flounder.physics.renderer;

import flounder.engine.*;
import flounder.models.*;
import flounder.physics.*;

import java.util.*;

/**
 * A manager for Boundings that want to be renderer.
 */
public class FlounderBounding implements IModule {
	private Map<Model, List<IBounding>> renderShapes;
	private boolean renders;
	private int aabbCount;

	/**
	 * Creates a new Boundings manager.
	 */
	public FlounderBounding() {
		renderShapes = new HashMap<>();
		renders = true;
		aabbCount = 0;
	}

	@Override
	public void init() {
	}

	@Override
	public void update() {
		aabbCount = renderShapes.size();
		clear(); // Clears before the next batch of rendering.
	}

	@Override
	public void profile() {
		FlounderEngine.getProfiler().add("Boundings", "Renderable", aabbCount);
	}

	/**
	 * Adds a shape to the render pool. (Run every frame).
	 *
	 * @param shape The shape to add.
	 */
	public void addShapeRender(IBounding shape) {
		for (Model model : renderShapes.keySet()) {
			if (model.equals(shape.getRenderModel())) {
				renderShapes.get(model).add(shape);
				return;
			}
		}

		List<IBounding> list = new ArrayList<>();
		list.add(shape);
		renderShapes.put(shape.getRenderModel(), list);
	}

	/**
	 * Gets a list of the renderable shapes.
	 *
	 * @return The renderable shapes.
	 */
	protected Map<Model, List<IBounding>> getRenderShapes() {
		return renderShapes;
	}

	public boolean renders() {
		return renders;
	}

	public void setRenders(boolean renders) {
		this.renders = renders;
	}

	/**
	 * Clears the renderable Boundings.
	 */
	protected void clear() {
		renderShapes.clear();
	}

	@Override
	public void dispose() {
		clear();
	}
}
