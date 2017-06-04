package flounder.physics.bounding;

import flounder.devices.*;
import flounder.framework.*;
import flounder.helpers.*;
import flounder.loaders.*;
import flounder.models.*;
import flounder.physics.*;

import java.util.*;

/**
 * A manager for Boundings that want to be renderer.
 */
public class FlounderBounding extends Module {
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

	@Module.Instance
	public static FlounderBounding get() {
		return (FlounderBounding) Framework.get().getInstance(FlounderBounding.class);
	}

	@Module.TabName
	public static String getTab() {
		return "Bounding";
	}
}
