package flounder.entities.components;

import flounder.entities.*;
import flounder.helpers.*;
import flounder.physics.*;

import javax.swing.*;

public class ComponentCollider extends IComponentEntity implements IComponentEditor {
	private QuickHull quickHull;

	public ComponentCollider(Entity entity) {
		this(entity, new QuickHull());
	}

	public ComponentCollider(Entity entity, QuickHull quickHull) {
		super(entity);
		this.quickHull = quickHull;
	}

	@Override
	public void update() {
		if (quickHull == null) {
			return;
		}

		if (getEntity().getComponent(ComponentAnimation.class) != null) {
			ComponentAnimation componentAnimation = (ComponentAnimation) getEntity().getComponent(ComponentAnimation.class);

			// Loads convex hull data from entity models.
			if (!quickHull.isLoaded() && componentAnimation.getModel().isLoaded()) {
				float[] vertices = componentAnimation.getModel().getMeshData().getVertices();
				quickHull.loadData(vertices);
			}

			if (getEntity().hasMoved()) {
				componentAnimation.getModel().getQuickHull().update(getEntity().getPosition(), getEntity().getRotation(), getEntity().getScale(), quickHull);
			}
		} else if (getEntity().getComponent(ComponentModel.class) != null) {
			ComponentModel componentModel = (ComponentModel) getEntity().getComponent(ComponentModel.class);

			// Loads convex hull data from entity models.
			if (!quickHull.isLoaded() && componentModel.getModel().isLoaded()) {
				float[] vertices = componentModel.getModel().getVertices();
				quickHull.loadData(vertices);
			}

			if (getEntity().hasMoved()) {
				componentModel.getModel().getQuickHull().update(getEntity().getPosition(), getEntity().getRotation(), getEntity().getScale(), quickHull);
			}
		}
	}

	@Override
	public void dispose() {
	}

	public QuickHull getQuickHull() {
		return quickHull;
	}

	@Override
	public void addToPanel(JPanel panel) {
	}

	@Override
	public void editorUpdate() {
	}

	@Override
	public Pair<String[], String[]> getSaveValues(String entityName) {
		return new Pair<>(
				new String[]{}, // Static variables
				new String[]{} // Class constructor
		);
	}
}
