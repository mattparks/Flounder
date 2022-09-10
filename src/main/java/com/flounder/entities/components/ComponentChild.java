package com.flounder.entities.components;

import com.flounder.entities.*;
import com.flounder.helpers.*;

import javax.swing.*;

public class ComponentChild extends IComponentEntity implements IComponentEditor {
	private Entity parent;
	private ChildRemoved childRemoved;

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
	 * @param parent The parent to this child.
	 */
	public ComponentChild(Entity entity, Entity parent) {
		super(entity);

		this.parent = parent;
		this.childRemoved = null;
	}

	/**
	 * Creates a new ComponentChild.
	 *
	 * @param entity The entity this component is attached to.
	 * @param parent The parent to this child.
	 * @param childRemoved A function called when the child has been removed.
	 */
	public ComponentChild(Entity entity, Entity parent, ChildRemoved childRemoved) {
		super(entity);

		this.parent = parent;
		this.childRemoved = childRemoved;
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

	public Entity getParent() {
		return parent;
	}

	@Override
	public void dispose() {
		if (childRemoved != null) {
			childRemoved.remove();
		}
	}

	@FunctionalInterface
	public interface ChildRemoved {
		void remove();
	}
}
