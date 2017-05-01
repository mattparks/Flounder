package flounder.entities.components;

import flounder.entities.*;
import flounder.helpers.*;
import flounder.shaders.*;

import javax.swing.*;

public class ComponentAlpha extends IComponentEntity implements IComponentAlpha, IComponentRender, IComponentEditor {
	private float alpha;

	/**
	 * Creates a new ComponentAlpha.
	 *
	 * @param entity The entity this component is attached to.
	 */
	public ComponentAlpha(Entity entity) {
		super(entity);
		this.alpha = 1.0f;
	}

	/**
	 * Creates a new ComponentAlpha.
	 *
	 * @param entity The entity this component is attached to.
	 */
	public ComponentAlpha(Entity entity, float alpha) {
		super(entity);
		this.alpha = alpha;
	}

	@Override
	public void update() {
	}

	@Override
	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	@Override
	public void render(ShaderObject shader, Single<Integer> vaoLength) {
		OpenGlUtils.cullBackFaces(alpha == 1.0f);
		OpenGlUtils.depthMask(false);
		shader.getUniformFloat("transparency").loadFloat(1.0f - alpha);
	}

	@Override
	public void renderClear(ShaderObject shader) {
		OpenGlUtils.cullBackFaces(true);
		OpenGlUtils.depthMask(true);
		shader.getUniformFloat("transparency").loadFloat(0.0f);
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
