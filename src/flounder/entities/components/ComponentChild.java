package flounder.entities.components;

import flounder.entities.*;
import flounder.helpers.*;

import javax.swing.*;

public class ComponentChild extends IComponentEntity implements IComponentEditor {
	private Entity parent;

	/**
	 * Creates a new ComponentChild.
	 *
	 * @param entity The entity this component is attached to.
	 */
	public ComponentChild(Entity entity) {
		this(entity, null);
	}

	/**
	 * Creates a new ComponentChild.
	 *
	 * @param entity The entity this component is attached to.
	 * @param parent
	 */
	public ComponentChild(Entity entity, Entity parent) {
		super(entity);

		this.parent = parent;
	}

	@Override
	public void update() {
		if (parent == null || parent.isRemoved()) {
			getEntity().forceRemove();
		}
	}

	@Override
	public void addToPanel(JPanel panel) {
	}

	@Override
	public void editorUpdate() {
	}

	@Override
	public Pair<String[], String[]> getSaveValues(String entityName) {
		//return new Pair<>(
		//		new String[]{}, // Static variables
		//		new String[]{} // Class constructor
		//);
		return null;
	}

	@Override
	public void dispose() {
	}
}
