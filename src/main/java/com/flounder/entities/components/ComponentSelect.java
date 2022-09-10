package com.flounder.entities.components;

import com.flounder.entities.*;
import com.flounder.helpers.*;
import com.flounder.shaders.*;

import javax.swing.*;

public class ComponentSelect extends IComponentEntity implements IComponentRender, IComponentEditor {
	private boolean selected;

	/**
	 * Creates a new ComponentSelect.
	 *
	 * @param entity The entity this component is attached to.
	 */
	public ComponentSelect(Entity entity) {
		super(entity);

		this.selected = false;
	}

	@Override
	public void update() {
		selected = false;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public void render(ShaderObject shader, Single<Integer> vaoLength) {
		if (selected) {
			shader.getUniformVec3("colourAddition").loadVec3(0.8f, -0.5f, -0.5f);
		} else {
			shader.getUniformVec3("colourAddition").loadVec3(0.0f, 0.0f, 0.0f);
		}
	}

	@Override
	public void renderClear(ShaderObject shader) {
		shader.getUniformVec3("colourAddition").loadVec3(0.0f, 0.0f, 0.0f);
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

	@Override
	public void dispose() {
	}
}
