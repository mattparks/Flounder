package flounder.physics.bounding;

import flounder.devices.*;
import flounder.framework.*;
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
	private static final FlounderBounding instance = new FlounderBounding();

	private Map<Model, List<IBounding>> renderShapes;
	private boolean renders;
	private int aabbCount;

	/**
	 * Creates a new bounding manager.
	 */
	public FlounderBounding() {
		super(ModuleUpdate.UPDATE_PRE, FlounderLogger.class, FlounderProfiler.class, FlounderDisplay.class, FlounderMouse.class, FlounderLoader.class);
	}

	@Override
	public void init() {
		this.renderShapes = new HashMap<>();
		this.renders = true;
		this.aabbCount = 0;
	}

	@Override
	public void run() {
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
	public IModule getInstance() {
		return instance;
	}

	@Override
	public void dispose() {
		clear();
	}
}
