package flounder.physics.renderer;

import flounder.devices.*;
import flounder.engine.*;
import flounder.loaders.*;
import flounder.logger.*;
import flounder.models.*;
import flounder.physics.*;
import flounder.profiling.*;

import java.util.*;

/**
 * A manager for Boundings that want to be renderer.
 */
public class FlounderBounding extends IModule {
	private static FlounderBounding instance;

	private Map<Model, List<IBounding>> renderShapes;
	private boolean renders;
	private int aabbCount;

	static {
		instance = new FlounderBounding();
	}

	private FlounderBounding() {
		super(FlounderLogger.class.getClass(), FlounderProfiler.class.getClass(), FlounderDisplay.class.getClass(), FlounderMouse.class.getClass(), FlounderLoader.class.getClass());
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
		FlounderProfiler.add("Boundings", "Renderable", aabbCount);
	}

	/**
	 * Adds a shape to the render pool. (Run every frame).
	 *
	 * @param shape The shape to add.
	 */
	public static void addShapeRender(IBounding shape) {
		for (Model model : instance.renderShapes.keySet()) {
			if (model.equals(shape.getRenderModel())) {
				instance.renderShapes.get(model).add(shape);
				return;
			}
		}

		List<IBounding> list = new ArrayList<>();
		list.add(shape);
		instance.renderShapes.put(shape.getRenderModel(), list);
	}

	/**
	 * Gets a list of the renderable shapes.
	 *
	 * @return The renderable shapes.
	 */
	protected static Map<Model, List<IBounding>> getRenderShapes() {
		return instance.renderShapes;
	}

	public static boolean renders() {
		return instance.renders;
	}

	public static void setRenders(boolean renders) {
		instance.renders = renders;
	}

	/**
	 * Clears the renderable Boundings.
	 */
	protected static void clear() {
		instance.renderShapes.clear();
	}

	@Override
	public void dispose() {
		clear();
	}
}
