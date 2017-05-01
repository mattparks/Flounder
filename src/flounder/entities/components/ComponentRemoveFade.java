package flounder.entities.components;

import flounder.entities.*;
import flounder.framework.*;
import flounder.helpers.*;
import flounder.shaders.*;
import flounder.visual.*;

import javax.swing.*;

public class ComponentRemoveFade extends IComponentEntity implements IComponentAlpha, IComponentRender, IComponentEditor {
	private float alpha;
	private ValueDriver driver;

	/**
	 * Creates a new ComponentRemoveFade.
	 *
	 * @param entity The entity this component is attached to.
	 */
	public ComponentRemoveFade(Entity entity) {
		super(entity);
		this.alpha = 1.0f;
		this.driver = new ConstantDriver(1.0f);
	}

	@Handler.Function(Handler.FLAG_UPDATE_PRE)
	public void update() {
		alpha = driver.update(Framework.getDelta());

		if (alpha == 0.0f) {
			getEntity().forceRemove();
		}
	}

	@Override
	public float getAlpha() {
		return alpha;
	}

	public void trigger() {
		if (driver == null || driver instanceof ConstantDriver) {
			driver = new SlideDriver(alpha, 0.0f, 0.3f);
		}
	}

	@Override
	public void render(ShaderObject shader, Single<Integer> vaoLength) {
		OpenGlUtils.cullBackFaces(alpha == 1.0f);
		shader.getUniformFloat("transparency").loadFloat(1.0f - alpha);
	}

	@Override
	public void renderClear(ShaderObject shader) {
		OpenGlUtils.cullBackFaces(true);
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

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
	}
}
