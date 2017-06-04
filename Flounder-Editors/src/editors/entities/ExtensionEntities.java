package editors.entities;

import editors.editor.*;
import flounder.devices.*;
import flounder.entities.*;
import flounder.entities.components.*;
import flounder.framework.*;
import flounder.helpers.*;
import flounder.maths.vectors.*;
import flounder.particles.*;
import flounder.physics.*;
import flounder.physics.bounding.*;
import flounder.shadows.*;
import flounder.skybox.*;

import javax.swing.*;

public class ExtensionEntities extends IEditorType {
	private static boolean ACTIVE = false;

	public boolean entityRotate;
	public boolean polygonMode;
	public String entityName;
	public String loadFromEntity;

	public Entity focusEntity;

	public ExtensionEntities() {
		super(FlounderDisplay.class, FlounderKeyboard.class, FlounderBounding.class, FlounderShadows.class, FlounderParticles.class, FlounderSkybox.class);
		ACTIVE = true;
	}

	@Override
	public void init() {
		// Sets the engine up for the editor.
		// FlounderProfiler.toggle(true);
		FlounderMouse.get().setCursorHidden(false);
		FlounderOpenGL.get().goWireframe(false);

		// Sets the world to constant fog and a sun.
		//	EbonWorld.addFog(new Fog(new Colour(1.0f, 1.0f, 1.0f), 0.003f, 2.0f, 0.0f, 50.0f));
		//	EbonWorld.addSun(new Light(new Colour(1.0f, 1.0f, 1.0f), new Vector3f(0.0f, 2000.0f, 2000.0f)));

		// Default editor values.
		entityRotate = false;
		polygonMode = false;
		entityName = "unnamed";
		loadDefaultEntity();
	}

	public void loadDefaultEntity() {
		if (focusEntity != null) {
			focusEntity.forceRemove();
		}

		focusEntity = new Entity(FlounderEntities.get().getEntities(), new Vector3f(), new Vector3f());
		/*new ComponentModel(
				focusEntity, 1.0f,
				ModelFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "cactus1", "model.obj")).create(),
				TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "cactus1", "diffuse.png")).create(),
				1
		);
		new ComponentSway(focusEntity, TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "cactus1", "sway.png")).create());
		new ComponentSurface(focusEntity, 1.0f, 0.0f, false, false);
		new ComponentCollider(focusEntity);
		new ComponentCollision(focusEntity);*/
		forceAddComponents();
	}

	@Override
	public void update() {
		// Updates wireframe modes.
		FlounderOpenGL.get().goWireframe(polygonMode);

		// Used to load a entity from a .entity file.
		if (loadFromEntity != null) {
			if (focusEntity != null) {
				focusEntity.forceRemove();
			}

			//focusEntity = FlounderEntities.load(loadFromEntity).createEntity(FlounderEntities.getEntities(), new Vector3f(), new Vector3f()); // TODO
			forceAddComponents();

			FrameEntities.nameField.setText(loadFromEntity);
			loadFromEntity = null;
		}

		// Updates the model to be focused, and rotated.
		if (focusEntity != null) {
			ComponentModel componentModel = (ComponentModel) focusEntity.getComponent(ComponentModel.class);

			if (componentModel != null && componentModel.getModel() != null && componentModel.getModel().getCollider() != null) {
				if (entityRotate) {
					focusEntity.getRotation().y += 20.0f * Framework.get().getDelta();
					focusEntity.setMoved();
				}

				float height = 0.0f;

				if (componentModel.getModel().getCollider() instanceof AABB) {
					height = -((AABB) componentModel.getModel().getCollider()).getCentreY() * componentModel.getScale() / 2.0f;
				} else if (componentModel.getModel().getCollider() instanceof Sphere) {
					height = -((Sphere) componentModel.getModel().getCollider()).getRadius() * componentModel.getScale();
				}

				if (focusEntity.getPosition().y != height) {
					focusEntity.getPosition().set(0.0f, height, 0.0f);
					focusEntity.setMoved();
				}
			}
		}
	}

	protected void setEntity(Entity entity) {
		if (this.focusEntity != null) {
			this.focusEntity.forceRemove();
			FrameEntities.clearSideTab();
		}

		this.focusEntity = entity;
		forceAddComponents();
	}

	private void forceAddComponents() {
		for (IComponentEntity component : focusEntity.getComponents()) {
			IComponentEditor editorComponent = (IComponentEditor) component;
			FrameEntities.editorComponents.add(editorComponent);

			if (editorComponent != null) {
				JPanel panel = IComponentEditor.makeTextPanel();
				editorComponent.addToPanel(panel);
				FrameEntities.componentAddRemove(panel, editorComponent);
				FrameEntities.addSideTab(IComponentEditor.getTabName(editorComponent), panel);
			}
		}
	}

	@Override
	public void profile() {
	}

	@Override
	public void dispose() {
		ACTIVE = false;
	}

	@Override
	public boolean isActive() {
		return ACTIVE;
	}
}
