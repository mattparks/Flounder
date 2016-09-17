package flounder.physics.renderer;

import flounder.engine.*;
import flounder.models.*;
import flounder.physics.*;

import java.util.*;

/**
 * A manager for AABB's that want to be renderer.
 */
public class FlounderShapes implements IModule {
	private Map<Model, List<IShape>> renderShapes;
	private boolean renders;
	private int aabbCount;

	/**
	 * Creates a new AABB manager.
	 */
	public FlounderShapes() {
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
		clear(); // Should have already been rendered.
	}

	@Override
	public void profile() {
		FlounderEngine.getProfiler().add("Shapes", "Renderable", aabbCount);
	}

	/**
	 * Adds a shape to the render pool. (Run every frame).
	 *
	 * @param shape The shape to add.
	 */
	public void addShapeRender(IShape shape) {
		for (Model model : renderShapes.keySet()) {
			if (model.equals(shape.getRenderModel())) {
				renderShapes.get(model).add(shape);
				return;
			}
		}

		List<IShape> list = new ArrayList<>();
		list.add(shape);
		renderShapes.put(shape.getRenderModel(), list);
	}

	/**
	 * Gets a list of the renderable shapes.
	 *
	 * @return The renderable shapes.
	 */
	protected Map<Model, List<IShape>> getRenderShapes() {
		return renderShapes;
	}

	public boolean renders() {
		return renders;
	}

	public void setRenders(boolean renders) {
		this.renders = renders;
	}

	/**
	 * Clears the renderable AABB's.
	 */
	protected void clear() {
		renderShapes.clear();
	}

	@Override
	public void dispose() {
		clear();
	}
}
