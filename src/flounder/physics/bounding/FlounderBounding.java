package flounder.physics.bounding;

import flounder.devices.*;
import flounder.framework.*;
import flounder.loaders.*;
import flounder.models.*;
import flounder.physics.*;
import flounder.profiling.*;

import java.util.*;

/**
 * A manager for Boundings that want to be renderer.
 */
public class FlounderBounding extends Module {
	private static final FlounderBounding INSTANCE = new FlounderBounding();
	public static final String PROFILE_TAB_NAME = "Bounding";

	private Map<ModelObject, List<Collider>> renderShapes;
	private boolean renders;
	private int boundingCount;

	/**
	 * Creates a new bounding manager.
	 */
	public FlounderBounding() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME, FlounderDisplay.class, FlounderMouse.class, FlounderModels.class, FlounderLoader.class);
	}

	@Override
	public void init() {
		this.renderShapes = new HashMap<>();
		this.boundingCount = 0;
	}

	@Override
	public void update() {
		boundingCount = renderShapes.size();
		clear(); // Clears before the next batch of rendering.
	}

	@Override
	public void profile() {
		FlounderProfiler.add(PROFILE_TAB_NAME, "Enabled", renders);
		FlounderProfiler.add(PROFILE_TAB_NAME, "Count", boundingCount);
	}

	/**
	 * Adds a shape to the render pool. (Run every frame).
	 *
	 * @param shape The shape to add.
	 */
	public static void addShapeRender(Collider shape) {
		if (!renders() || shape == null) {
			return;
		}

		for (ModelObject model : INSTANCE.renderShapes.keySet()) {
			if (model.equals(shape.getRenderModel())) {
				INSTANCE.renderShapes.get(model).add(shape);
				return;
			}
		}

		List<Collider> list = new ArrayList<>();
		list.add(shape);
		INSTANCE.renderShapes.put(shape.getRenderModel(), list);
	}

	/**
	 * Gets a list of the renderable shapes.
	 *
	 * @return The renderable shapes.
	 */
	protected static Map<ModelObject, List<Collider>> getRenderShapes() {
		return INSTANCE.renderShapes;
	}

	public static boolean renders() {
		return INSTANCE.renders;
	}

	public static void toggle(boolean renders) {
		INSTANCE.renders = renders;
	}

	/**
	 * Clears the renderable Boundings.
	 */
	protected static void clear() {
		INSTANCE.renderShapes.clear();
	}

	@Override
	public Module getInstance() {
		return INSTANCE;
	}

	@Override
	public void dispose() {
		this.renders = true;
		clear();
	}
}
